package com.switchone.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal fromAmount;

    @Column(nullable = false, length = 3)
    private String fromCurrency;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal toAmount;

    @Column(nullable = false, length = 3)
    private String toCurrency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tradeRate;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    public static Order create(BigDecimal fromAmount, String fromCurrency,
                               BigDecimal toAmount, String toCurrency,
                               BigDecimal tradeRate, LocalDateTime dateTime) {
        return Order.builder()
                .fromAmount(fromAmount)
                .fromCurrency(fromCurrency)
                .toAmount(toAmount)
                .toCurrency(toCurrency)
                .tradeRate(tradeRate)
                .dateTime(dateTime)
                .build();
    }
}
