package com.finbank.transaction.dto;

import org.springframework.http.HttpStatus;

/** Ket qua chuyen tien: ban ghi transaction da luu va HTTP status tra ve cho client. */
public record TransferResult(HttpStatus httpStatus, TransactionResponse transaction) {
}
