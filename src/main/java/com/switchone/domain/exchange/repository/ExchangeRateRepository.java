package com.switchone.domain.exchange.repository;

import com.switchone.domain.exchange.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
}
