package com.finbank.transaction.dto;

import java.math.BigDecimal;

public record BalanceDto(String accountNumber, BigDecimal balance) {
}
