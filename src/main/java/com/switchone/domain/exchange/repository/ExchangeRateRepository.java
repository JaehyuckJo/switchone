package com.switchone.domain.exchange.repository;

import com.switchone.domain.exchange.entity.ExchangeRate;
import com.switchone.domain.exchange.enumtype.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findTopByCurrencyOrderByDateTimeDesc(CurrencyCode currency);
}
