package com.switchone.application.order.dto.request;

import java.math.BigDecimal;

public record OrderRequest(
        BigDecimal forexAmount,
        String fromCurrency,
        String toCurrency
) {}
