package com.switchone.application.exchange.facade;

import com.switchone.application.exchange.dto.response.LatestExchangeRateResponse;
import com.switchone.common.exception.BusinessException;
import com.switchone.common.exception.ErrorCode;
import com.switchone.domain.exchange.entity.ExchangeRate;
import com.switchone.domain.exchange.enumtype.CurrencyCode;
import com.switchone.domain.exchange.repository.ExchangeRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExchangeRateFacadeTest {

    @Mock
    private RestClient koreaEximRestClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateFacade exchangeRateFacade;

    @Test
    @DisplayName("특정 통화 최신 환율 조회")
    void getLatestExchangeRate_성공() {
        ExchangeRate usdRate = ExchangeRate.create(
                CurrencyCode.USD,
                new BigDecimal("1480.43"),
                new BigDecimal("1474.47"),
                new BigDecimal("1477.45")
        );
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.of(usdRate));

        LatestExchangeRateResponse.ExchangeRateItem result =
                exchangeRateFacade.getLatestExchangeRate(CurrencyCode.USD);

        assertThat(result.currency()).isEqualTo(CurrencyCode.USD);
        assertThat(result.buyRate()).isEqualByComparingTo(new BigDecimal("1480.43"));
        assertThat(result.sellRate()).isEqualByComparingTo(new BigDecimal("1474.47"));
        assertThat(result.tradeStanRate()).isEqualByComparingTo(new BigDecimal("1477.45"));
    }

    @Test
    @DisplayName("환율 데이터 없을 시 예외")
    void getLatestExchangeRate_데이터없음_예외() {
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> exchangeRateFacade.getLatestExchangeRate(CurrencyCode.USD))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.EXCHANGE_RATE_NOT_FOUND));
    }

    @Test
    @DisplayName("전체 최신 환율 조회")
    void getLatestExchangeRates_일부통화만_존재() {
        ExchangeRate usdRate = ExchangeRate.create(CurrencyCode.USD,
                new BigDecimal("1480.43"), new BigDecimal("1474.47"), new BigDecimal("1477.45"));
        ExchangeRate jpyRate = ExchangeRate.create(CurrencyCode.JPY,
                new BigDecimal("9.15"), new BigDecimal("9.05"), new BigDecimal("9.10"));

        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.of(usdRate));
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.JPY))
                .willReturn(Optional.of(jpyRate));
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.EUR))
                .willReturn(Optional.empty());
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.CNY))
                .willReturn(Optional.empty());

        LatestExchangeRateResponse response = exchangeRateFacade.getLatestExchangeRates();

        assertThat(response.exchangeRateList()).hasSize(2);
        List<CurrencyCode> currencies = response.exchangeRateList().stream()
                .map(LatestExchangeRateResponse.ExchangeRateItem::currency)
                .toList();
        assertThat(currencies).containsExactlyInAnyOrder(CurrencyCode.USD, CurrencyCode.JPY);
    }

    @Test
    @DisplayName("전체 최신 환율 조회: 데이터 없으면 빈 리스트 반환")
    void getLatestExchangeRates_데이터없음_빈리스트() {
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(any()))
                .willReturn(Optional.empty());

        LatestExchangeRateResponse response = exchangeRateFacade.getLatestExchangeRates();

        assertThat(response.exchangeRateList()).isEmpty();
    }

    private static <T> T any() {
        return org.mockito.ArgumentMatchers.any();
    }
}
