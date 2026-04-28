package com.switchone.presentation.exchange;

import com.switchone.application.exchange.dto.response.LatestExchangeRateResponse;
import com.switchone.application.exchange.facade.ExchangeRateFacade;
import com.switchone.common.exception.BusinessException;
import com.switchone.common.exception.ErrorCode;
import com.switchone.common.response.ApiResponse;
import com.switchone.domain.exchange.enumtype.CurrencyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange-rate")
public class ExchangeController {

    private final ExchangeRateFacade exchangeRateFacade;

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<LatestExchangeRateResponse>> getLatestExchangeAllRate() {
        return ResponseEntity.ok(ApiResponse.ok(exchangeRateFacade.getLatestExchangeRates()));
    }

    @GetMapping("/latest/{currency}")
    public ResponseEntity<ApiResponse<LatestExchangeRateResponse.ExchangeRateItem>> getLatestExchangeRate(
            @PathVariable String currency) {
        CurrencyCode currencyCode;
        try {
            currencyCode = CurrencyCode.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY);
        }
        return ResponseEntity.ok(ApiResponse.ok(exchangeRateFacade.getLatestExchangeRate(currencyCode)));
    }
}
