package com.finbank.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmountRequest(
        @NotNull(message = "amount không được để trống")
        @Positive(message = "amount phải lớn hơn 0") BigDecimal amount) {
}
