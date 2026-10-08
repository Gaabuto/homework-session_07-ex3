package com.finbank.transaction.service;

import com.finbank.transaction.client.AccountClient;
import com.finbank.transaction.dto.BalanceDto;
import com.finbank.transaction.dto.TransactionResponse;
import com.finbank.transaction.dto.TransferRequest;
import com.finbank.transaction.dto.TransferResult;
import com.finbank.transaction.entity.Transaction;
import com.finbank.transaction.entity.TransactionStatus;
import com.finbank.transaction.exception.TransactionNotFoundException;
import com.finbank.transaction.exception.TransferFailedException;
import com.finbank.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountClient accountClient;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountClient accountClient, TransactionRepository transactionRepository) {
        this.accountClient = accountClient;
        this.transactionRepository = transactionRepository;
    }

    public TransferResult transfer(TransferRequest request) {
        String from = request.fromAccountNumber();
        String to = request.toAccountNumber();
        try {
            if (from.equals(to)) {
                throw new TransferFailedException(HttpStatus.BAD_REQUEST,
                        "Tài khoản nguồn và tài khoản đích không được trùng nhau");
            }

            // Buoc 2: tai khoan nguon phai ton tai va du so du
            accountClient.getAccount(from);
            BalanceDto balance = accountClient.getBalance(from);
            if (balance.balance().compareTo(request.amount()) < 0) {
                throw new TransferFailedException(HttpStatus.BAD_REQUEST,
                        "Tài khoản " + from + " không đủ số dư (số dư: " + balance.balance().toPlainString()
                                + ", cần: " + request.amount().toPlainString() + ")");
            }

            // Buoc 3: tai khoan dich phai ton tai
            accountClient.getAccount(to);

            // Buoc 4: tru tien nguon, cong tien dich
            accountClient.debit(from, request.amount());
            creditOrRefund(from, to, request);

            // Buoc 5: luu giao dich thanh cong
            Transaction saved = save(request, TransactionStatus.SUCCESS, "Chuyển tiền thành công");
            return new TransferResult(HttpStatus.CREATED, TransactionResponse.from(saved));
        } catch (TransferFailedException ex) {
            Transaction saved = save(request, TransactionStatus.FAILED, ex.getMessage());
            return new TransferResult(ex.getStatus(), TransactionResponse.from(saved));
        }
    }

    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll().stream().map(TransactionResponse::from).toList();
    }

    public TransactionResponse findById(Long id) {
        return transactionRepository.findById(id)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    /**
     * Cong tien vao tai khoan dich. Neu that bai (da tru tien nguon roi) thi cong tra lai
     * tai khoan nguon, tranh tinh trang tien bi tru ma khong den dich.
     */
    private void creditOrRefund(String from, String to, TransferRequest request) {
        try {
            accountClient.credit(to, request.amount());
        } catch (TransferFailedException creditError) {
            try {
                accountClient.credit(from, request.amount());
            } catch (TransferFailedException refundError) {
                log.error("Hoan tien that bai: tai khoan {} da bi tru {} nhung chua duoc hoan lai",
                        from, request.amount(), refundError);
                throw new TransferFailedException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Cộng tiền vào tài khoản đích thất bại và hoàn tiền thất bại, cần xử lý thủ công: "
                                + creditError.getMessage());
            }
            throw new TransferFailedException(creditError.getStatus(),
                    "Cộng tiền vào tài khoản đích thất bại, đã hoàn tiền về tài khoản nguồn: "
                            + creditError.getMessage());
        }
    }

    private Transaction save(TransferRequest request, TransactionStatus status, String message) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber(request.fromAccountNumber());
        transaction.setToAccountNumber(request.toAccountNumber());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setStatus(status);
        transaction.setMessage(message);
        return transactionRepository.save(transaction);
    }
}
