package com.finbank.transaction.dto;

import com.finbank.transaction.entity.Transaction;
import com.finbank.transaction.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String description,
        TransactionStatus status,
        String message,
        LocalDateTime createdAt) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getFromAccountNumber(),
                transaction.getToAccountNumber(), transaction.getAmount(), transaction.getDescription(),
                transaction.getStatus(), transaction.getMessage(), transaction.getCreatedAt());
    }
}
