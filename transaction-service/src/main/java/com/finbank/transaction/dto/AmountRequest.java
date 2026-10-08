package com.finbank.transaction.dto;

import java.math.BigDecimal;

/** Body gui sang PUT /api/accounts/{accountNumber}/debit va /credit. */
public record AmountRequest(BigDecimal amount) {
}
