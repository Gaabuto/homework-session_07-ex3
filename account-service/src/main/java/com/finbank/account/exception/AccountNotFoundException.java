package com.finbank.account.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Không tìm thấy tài khoản " + accountNumber);
    }
}
