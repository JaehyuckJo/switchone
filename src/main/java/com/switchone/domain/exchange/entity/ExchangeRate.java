package com.switchone.domain.exchange.entity;

import com.switchone.domain.exchange.enumtype.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal buyRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tradeStandardRate;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    public static ExchangeRate create(CurrencyCode currency,
                                      BigDecimal buyRate,
                                      BigDecimal sellRate,
                                      BigDecimal tradeStandardRate)
                                       {
        return ExchangeRate.builder()
                .currency(currency)
                .buyRate(buyRate)
                .sellRate(sellRate)
                .tradeStandardRate(tradeStandardRate)
                .dateTime(LocalDateTime.now().withNano(0))
                .build();
    }
}