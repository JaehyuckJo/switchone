package com.switchone.application.order.facade;

import com.switchone.application.order.dto.request.OrderRequest;
import com.switchone.application.order.dto.response.OrderListResponse;
import com.switchone.application.order.dto.response.OrderResponse;
import com.switchone.domain.exchange.entity.ExchangeRate;
import com.switchone.domain.exchange.enumtype.CurrencyCode;
import com.switchone.domain.exchange.repository.ExchangeRateRepository;
import com.switchone.common.exception.BusinessException;
import com.switchone.common.exception.ErrorCode;
import com.switchone.domain.order.entity.Order;
import com.switchone.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final ExchangeRateRepository exchangeRateRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse order(OrderRequest request) {
        boolean fromKrw = "KRW".equalsIgnoreCase(request.fromCurrency());
        boolean toKrw = "KRW".equalsIgnoreCase(request.toCurrency());
        if (fromKrw == toKrw) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY_PAIR);
        }

        boolean isBuy = fromKrw;
        String forexCurrencyStr = isBuy ? request.toCurrency() : request.fromCurrency();
        CurrencyCode forexCurrency;
        try {
            forexCurrency = CurrencyCode.valueOf(forexCurrencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_CURRENCY);
        }

        ExchangeRate rate = exchangeRateRepository.findTopByCurrencyOrderByDateTimeDesc(forexCurrency)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXCHANGE_RATE_NOT_FOUND));

        BigDecimal fromAmount;
        BigDecimal toAmount;
        BigDecimal tradeRate;

        if (isBuy) {
            tradeRate = rate.getBuyRate();
            fromAmount = request.forexAmount().multiply(tradeRate).setScale(0, RoundingMode.FLOOR);
            toAmount = request.forexAmount();
        } else {
            tradeRate = rate.getSellRate();
            fromAmount = request.forexAmount();
            toAmount = request.forexAmount().multiply(tradeRate).setScale(0, RoundingMode.FLOOR);
        }

        String fromCurrency = request.fromCurrency().toUpperCase();
        String toCurrency = request.toCurrency().toUpperCase();

        orderRepository.save(Order.create(fromAmount, fromCurrency, toAmount, toCurrency, tradeRate, rate.getDateTime()));

        return new OrderResponse(fromAmount, fromCurrency, toAmount, toCurrency, tradeRate, rate.getDateTime());
    }

    @Transactional(readOnly = true)
    public OrderListResponse getOrderList() {
        List<OrderListResponse.OrderItem> orderList = orderRepository.findAll().stream()
                .map(o -> new OrderListResponse.OrderItem(
                        o.getId(),
                        o.getFromAmount(),
                        o.getFromCurrency(),
                        o.getToAmount(),
                        o.getToCurrency(),
                        o.getTradeRate(),
                        o.getDateTime()
                ))
                .toList();
        return new OrderListResponse(orderList);
    }
}
