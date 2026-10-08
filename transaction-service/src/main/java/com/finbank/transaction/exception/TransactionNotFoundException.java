package com.finbank.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long id) {
        super("Không tìm thấy giao dịch " + id);
    }
}
