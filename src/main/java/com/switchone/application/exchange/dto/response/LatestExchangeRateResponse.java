package com.switchone.application.exchange.dto.response;

import com.switchone.domain.exchange.enumtype.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LatestExchangeRateResponse(List<ExchangeRateItem> exchangeRateList) {

    public record ExchangeRateItem(
            CurrencyCode currency,
            BigDecimal buyRate,
            BigDecimal tradeStanRate,
            BigDecimal sellRate,
            LocalDateTime dateTime
    ) {}
}
