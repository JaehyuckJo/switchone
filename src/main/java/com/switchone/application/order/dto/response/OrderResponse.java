package com.switchone.application.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        BigDecimal fromAmount,
        String fromCurrency,
        BigDecimal toAmount,
        String toCurrency,
        BigDecimal tradeRate,
        LocalDateTime dateTime
) {}
