    package com.switchone.application.exchange.facade;

    import com.switchone.application.exchange.dto.response.ExchangeRateResponse;
    import com.switchone.application.exchange.dto.response.LatestExchangeRateResponse;
    import com.switchone.common.exception.BusinessException;
    import com.switchone.common.exception.ErrorCode;
    import com.switchone.domain.exchange.entity.ExchangeRate;
    import com.switchone.domain.exchange.enumtype.CurrencyCode;
    import com.switchone.domain.exchange.repository.ExchangeRateRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.client.RestClient;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.Collections;
    import java.util.EnumSet;
    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class ExchangeRateFacade {

        private static final DateTimeFormatter SEARCH_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
        private static final EnumSet<CurrencyCode> TARGET_CURRENCIES =
                EnumSet.of(CurrencyCode.USD, CurrencyCode.JPY, CurrencyCode.EUR, CurrencyCode.CNY);

        private final RestClient koreaEximRestClient;
        private final ExchangeRateRepository exchangeRateRepository;

        @Value("${external.koreaexim.auth-key}")
        private String authKey;

        @Value("${external.koreaexim.data:AP01}")
        private String dataType;

        @Transactional
        public void fetchLatestExchangeRates() {
            for (int daysBack = 0; daysBack <= 7; daysBack++) {
                String searchDate = LocalDate.now().minusDays(daysBack).format(SEARCH_DATE_FORMATTER);

                List<ExchangeRateResponse> responses = koreaEximRestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/exchangeJSON")
                                .queryParam("authkey", authKey)
                                .queryParam("searchdate", searchDate)
                                .queryParam("data", dataType)
                                .build())
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                List<ExchangeRate> exchangeRates = Optional.ofNullable(responses)
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(r -> Integer.valueOf(1).equals(r.result()))
                        .map(this::toExchangeRate)
                        .flatMap(Optional::stream)
                        .toList();

                if (!exchangeRates.isEmpty()) {
                    exchangeRateRepository.saveAll(exchangeRates);
                    return;
                }
            }
        }

        private Optional<ExchangeRate> toExchangeRate(ExchangeRateResponse response) {
            CurrencyCode currencyCode = resolveCurrencyCode(response.cur_unit());
            if (currencyCode == null || !TARGET_CURRENCIES.contains(currencyCode)) {
                return Optional.empty();
            }

            return Optional.of(ExchangeRate.create(
                    currencyCode,
                    parseRate(response.tts()),
                    parseRate(response.ttb()),
                    parseRate(response.deal_bas_r())
            ));
        }

        private CurrencyCode resolveCurrencyCode(String curUnit) {
            if (curUnit == null || curUnit.isBlank()) {
                return null;
            }

            String normalized = curUnit.trim();
            int suffixIndex = normalized.indexOf('(');
            String code = suffixIndex > 0 ? normalized.substring(0, suffixIndex).trim() : normalized;
            if(code.equalsIgnoreCase("CNH")) return CurrencyCode.CNY;
            try {
                return CurrencyCode.valueOf(code.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private BigDecimal parseRate(String rawRate) {
            if (rawRate == null || rawRate.isBlank()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(rawRate.replace(",", ""));
        }

        @Transactional(readOnly = true)
        public LatestExchangeRateResponse.ExchangeRateItem getLatestExchangeRate(CurrencyCode currency) {
            return exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(currency)
                    .map(rate -> new LatestExchangeRateResponse.ExchangeRateItem(
                            rate.getCurrency(),
                            rate.getBuyRate(),
                            rate.getTradeStandardRate(),
                            rate.getSellRate(),
                            rate.getDateTime()
                    ))
                    .orElseThrow(() -> new BusinessException(ErrorCode.EXCHANGE_RATE_NOT_FOUND));
        }

        @Transactional(readOnly = true)
        public LatestExchangeRateResponse getLatestExchangeRates() {
            List<LatestExchangeRateResponse.ExchangeRateItem> items = TARGET_CURRENCIES.stream()
                    .map(currency -> exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(currency))
                    .flatMap(Optional::stream)
                    .map(rate -> new LatestExchangeRateResponse.ExchangeRateItem(
                            rate.getCurrency(),
                            rate.getBuyRate(),
                            rate.getTradeStandardRate(),
                            rate.getSellRate(),
                            rate.getDateTime()
                    ))
                    .toList();
            return new LatestExchangeRateResponse(items);
        }

    }


