package com.finbank.account.dto;

import com.finbank.account.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getOwnerName(),
                account.getBalance(), account.getCreatedAt(), account.getUpdatedAt());
    }
}
