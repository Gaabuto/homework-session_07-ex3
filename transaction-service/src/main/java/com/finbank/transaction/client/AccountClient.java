package com.finbank.transaction.client;

import com.finbank.transaction.dto.AccountDto;
import com.finbank.transaction.dto.AmountRequest;
import com.finbank.transaction.dto.BalanceDto;
import com.finbank.transaction.dto.ErrorResponse;
import com.finbank.transaction.exception.TransferFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * Goi Account Service qua RestTemplate.
 * URL dung ten service dang ky tren Eureka ("account-service"), khong hard-code host:port.
 */
@Component
public class AccountClient {

    private static final Logger log = LoggerFactory.getLogger(AccountClient.class);

    private static final String ACCOUNT_SERVICE_URL = "http://account-service/api/accounts";

    private final RestTemplate restTemplate;

    public AccountClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AccountDto getAccount(String accountNumber) {
        return call(accountNumber, () -> restTemplate.getForObject(
                ACCOUNT_SERVICE_URL + "/{accountNumber}", AccountDto.class, accountNumber));
    }

    public BalanceDto getBalance(String accountNumber) {
        return call(accountNumber, () -> restTemplate.getForObject(
                ACCOUNT_SERVICE_URL + "/{accountNumber}/balance", BalanceDto.class, accountNumber));
    }

    public AccountDto debit(String accountNumber, BigDecimal amount) {
        return updateBalance(accountNumber, "debit", amount);
    }

    public AccountDto credit(String accountNumber, BigDecimal amount) {
        return updateBalance(accountNumber, "credit", amount);
    }

    private AccountDto updateBalance(String accountNumber, String action, BigDecimal amount) {
        HttpEntity<AmountRequest> body = new HttpEntity<>(new AmountRequest(amount));
        return call(accountNumber, () -> restTemplate.exchange(
                ACCOUNT_SERVICE_URL + "/{accountNumber}/" + action, HttpMethod.PUT, body,
                AccountDto.class, accountNumber).getBody());
    }

    /** Thuc hien request va chuyen cac loi cua RestTemplate thanh TransferFailedException co thong bao ro rang. */
    private <T> T call(String accountNumber, Supplier<T> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new TransferFailedException(HttpStatus.NOT_FOUND,
                    messageFrom(ex, "Tài khoản " + accountNumber + " không tồn tại"));
        } catch (HttpClientErrorException ex) {
            // 400 tu Account Service, vi du: khong du so du khi tru tien
            throw new TransferFailedException(HttpStatus.BAD_REQUEST,
                    messageFrom(ex, "Account Service từ chối yêu cầu cho tài khoản " + accountNumber));
        } catch (HttpServerErrorException ex) {
            log.error("Account Service tra ve loi {}", ex.getStatusCode(), ex);
            throw new TransferFailedException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Account Service gặp lỗi, vui lòng thử lại sau");
        } catch (ResourceAccessException ex) {
            // Khong ket noi duoc hoac qua thoi gian read-timeout
            log.error("Khong goi duoc Account Service: {}", ex.getMessage());
            throw new TransferFailedException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Account Service không phản hồi, vui lòng thử lại sau");
        } catch (IllegalStateException ex) {
            // LoadBalancer khong tim thay instance nao cua account-service tren Eureka
            log.error("Khong tim thay instance account-service: {}", ex.getMessage());
            throw new TransferFailedException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Account Service hiện không khả dụng, vui lòng thử lại sau");
        }
    }

    /** Lay truong "message" trong body loi cua Account Service, neu khong doc duoc thi dung thong bao mac dinh. */
    private String messageFrom(RestClientResponseException ex, String defaultMessage) {
        try {
            ErrorResponse error = ex.getResponseBodyAs(ErrorResponse.class);
            if (error != null && error.message() != null) {
                return error.message();
            }
        } catch (RuntimeException ignored) {
            // body khong phai JSON ErrorResponse
        }
        return defaultMessage;
    }
}
