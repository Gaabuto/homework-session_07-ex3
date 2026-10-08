package com.finbank.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "fromAccountNumber không được để trống") String fromAccountNumber,
        @NotBlank(message = "toAccountNumber không được để trống") String toAccountNumber,
        @NotNull(message = "amount không được để trống")
        @Positive(message = "amount phải lớn hơn 0") BigDecimal amount,
        String description) {
}
