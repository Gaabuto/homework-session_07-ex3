package com.finbank.account.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String accountNumber) {
        super("Tài khoản " + accountNumber + " đã tồn tại");
    }
}
