package com.finbank.transaction.dto;

import java.math.BigDecimal;

/** Du lieu tai khoan nhan ve tu Account Service. */
public record AccountDto(Long id, String accountNumber, String ownerName, BigDecimal balance) {
}
