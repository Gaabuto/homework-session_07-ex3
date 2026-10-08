package com.finbank.account.service;

import com.finbank.account.dto.AccountResponse;
import com.finbank.account.dto.BalanceResponse;
import com.finbank.account.dto.CreateAccountRequest;
import com.finbank.account.entity.Account;
import com.finbank.account.exception.AccountNotFoundException;
import com.finbank.account.exception.DuplicateAccountException;
import com.finbank.account.exception.InsufficientBalanceException;
import com.finbank.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new DuplicateAccountException(request.accountNumber());
        }
        Account account = new Account();
        account.setAccountNumber(request.accountNumber());
        account.setOwnerName(request.ownerName());
        account.setBalance(request.balance());
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getByAccountNumber(String accountNumber) {
        return AccountResponse.from(findAccount(accountNumber));
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String accountNumber) {
        Account account = findAccount(accountNumber);
        return new BalanceResponse(account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public AccountResponse debit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        // Kiem tra lai so du ngay tai day: Transaction Service da kiem tra truoc,
        // nhung so du co the thay doi giua 2 lan goi
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(accountNumber, account.getBalance(), amount);
        }
        account.setBalance(account.getBalance().subtract(amount));
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse credit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        account.setBalance(account.getBalance().add(amount));
        return AccountResponse.from(account);
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
