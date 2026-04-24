package com.switchone.infrastructure.scheduler;

import com.switchone.application.exchange.facade.ExchangeRateFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {

    private final ExchangeRateFacade exchangeRateFacade;

    @Scheduled(fixedRate = 60000)
    public void refreshExchangeRates() {
        log.info("환율 갱신 스케줄러 시작");

        try {
            exchangeRateFacade.fetchLatestExchangeRates();
            log.info("환율 갱신 완료");
        } catch (Exception e) {
            log.error("환율 갱신 실패", e);
        }
    }
}
