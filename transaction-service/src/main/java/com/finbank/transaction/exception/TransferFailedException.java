package com.finbank.transaction.exception;

import org.springframework.http.HttpStatus;

/** Loi trong qua trinh chuyen tien, kem HTTP status se tra ve cho client. */
public class TransferFailedException extends RuntimeException {

    private final HttpStatus status;

    public TransferFailedException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
