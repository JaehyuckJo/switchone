package com.switchone.application.order.facade;

import com.switchone.application.order.dto.request.OrderRequest;
import com.switchone.application.order.dto.response.OrderListResponse;
import com.switchone.application.order.dto.response.OrderResponse;
import com.switchone.common.exception.BusinessException;
import com.switchone.common.exception.ErrorCode;
import com.switchone.domain.exchange.entity.ExchangeRate;
import com.switchone.domain.exchange.enumtype.CurrencyCode;
import com.switchone.domain.exchange.repository.ExchangeRateRepository;
import com.switchone.domain.order.entity.Order;
import com.switchone.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderFacade orderFacade;

    private ExchangeRate usdRate;

    @BeforeEach
    void setUp() {
        usdRate = ExchangeRate.create(
                CurrencyCode.USD,
                new BigDecimal("1480.43"),  // buyRate
                new BigDecimal("1474.47"),  // sellRate
                new BigDecimal("1477.45")   // tradeStandardRate
        );
    }

    @Test
    @DisplayName("KRW → 외화 매수: buyRate 적용, fromAmount = forexAmount * buyRate")
    void order_KRW매수_성공() {
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.of(usdRate));

        OrderRequest request = new OrderRequest(new BigDecimal("200"), "KRW", "USD");
        OrderResponse response = orderFacade.order(request);

        assertThat(response.fromCurrency()).isEqualTo("KRW");
        assertThat(response.toCurrency()).isEqualTo("USD");
        assertThat(response.toAmount()).isEqualByComparingTo(new BigDecimal("200"));
        assertThat(response.fromAmount()).isEqualByComparingTo(new BigDecimal("296086")); // 200 * 1480.43 floor
        assertThat(response.tradeRate()).isEqualByComparingTo(new BigDecimal("1480.43"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("외화 → KRW 매도: sellRate 적용, toAmount = forexAmount * sellRate")
    void order_외화매도_성공() {
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.of(usdRate));

        OrderRequest request = new OrderRequest(new BigDecimal("133"), "USD", "KRW");
        OrderResponse response = orderFacade.order(request);

        assertThat(response.fromCurrency()).isEqualTo("USD");
        assertThat(response.toCurrency()).isEqualTo("KRW");
        assertThat(response.fromAmount()).isEqualByComparingTo(new BigDecimal("133"));
        assertThat(response.toAmount()).isEqualByComparingTo(new BigDecimal("196104")); // 133 * 1474.47 floor
        assertThat(response.tradeRate()).isEqualByComparingTo(new BigDecimal("1474.47"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("KRW → KRW 요청 시 INVALID_CURRENCY_PAIR 예외")
    void order_KRW_to_KRW_예외() {
        OrderRequest request = new OrderRequest(new BigDecimal("100"), "KRW", "KRW");

        assertThatThrownBy(() -> orderFacade.order(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CURRENCY_PAIR));
    }

    @Test
    @DisplayName("외화 → 외화 요청 시 INVALID_CURRENCY_PAIR 예외")
    void order_외화간_거래_예외() {
        OrderRequest request = new OrderRequest(new BigDecimal("100"), "USD", "EUR");

        assertThatThrownBy(() -> orderFacade.order(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CURRENCY_PAIR));
    }

    @Test
    @DisplayName("지원하지 않는 통화 코드 시 INVALID_CURRENCY 예외")
    void order_지원안하는통화_예외() {
        OrderRequest request = new OrderRequest(new BigDecimal("100"), "KRW", "XYZ");

        assertThatThrownBy(() -> orderFacade.order(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CURRENCY));
    }

    @Test
    @DisplayName("환율 데이터 없을 시 EXCHANGE_RATE_NOT_FOUND 예외")
    void order_환율없음_예외() {
        given(exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(CurrencyCode.USD))
                .willReturn(Optional.empty());

        OrderRequest request = new OrderRequest(new BigDecimal("100"), "KRW", "USD");

        assertThatThrownBy(() -> orderFacade.order(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.EXCHANGE_RATE_NOT_FOUND));
    }

    @Test
    @DisplayName("주문 내역 조회: 저장된 주문 목록 반환")
    void getOrderList_성공() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 22, 10, 1, 0);
        Order order1 = Order.builder()
                .id(1L).fromAmount(new BigDecimal("296086")).fromCurrency("KRW")
                .toAmount(new BigDecimal("200")).toCurrency("USD")
                .tradeRate(new BigDecimal("1480.43")).dateTime(dateTime)
                .build();
        Order order2 = Order.builder()
                .id(2L).fromAmount(new BigDecimal("133")).fromCurrency("USD")
                .toAmount(new BigDecimal("196104")).toCurrency("KRW")
                .tradeRate(new BigDecimal("1474.47")).dateTime(dateTime)
                .build();

        given(orderRepository.findAll()).willReturn(List.of(order1, order2));

        OrderListResponse response = orderFacade.getOrderList();

        assertThat(response.orderList()).hasSize(2);
        assertThat(response.orderList().get(0).id()).isEqualTo(1L);
        assertThat(response.orderList().get(0).fromCurrency()).isEqualTo("KRW");
        assertThat(response.orderList().get(1).id()).isEqualTo(2L);
        assertThat(response.orderList().get(1).toCurrency()).isEqualTo("KRW");
    }
}
