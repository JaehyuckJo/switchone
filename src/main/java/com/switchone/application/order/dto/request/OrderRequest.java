package com.switchone.application.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull(message = "환전 금액을 입력해주세요")
        @Positive(message = "환전 금액은 0보다 커야 합니다")
        BigDecimal forexAmount,

        @NotBlank(message = "출발 통화를 입력해주세요")
        String fromCurrency,

        @NotBlank(message = "도착 통화를 입력해주세요")
        String toCurrency
) {}
