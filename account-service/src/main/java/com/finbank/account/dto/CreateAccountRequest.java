package com.finbank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "accountNumber không được để trống") String accountNumber,
        @NotBlank(message = "ownerName không được để trống") String ownerName,
        @NotNull(message = "balance không được để trống")
        @PositiveOrZero(message = "balance phải >= 0") BigDecimal balance) {
}
