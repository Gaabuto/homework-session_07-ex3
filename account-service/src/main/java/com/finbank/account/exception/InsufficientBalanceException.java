package com.finbank.account.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String accountNumber, BigDecimal balance, BigDecimal amount) {
        super("Tài khoản " + accountNumber + " không đủ số dư (số dư: " + balance.toPlainString()
                + ", cần: " + amount.toPlainString() + ")");
    }
}
