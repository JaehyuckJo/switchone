package com.switchone.presentation.exchange;

import com.switchone.application.exchange.dto.response.ExchangeRateResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/exchange-rate")
public class ExchangeController {

    @GetMapping("/latest")
    public ResponseEntity<ExchangeRateResponse> getLatestExchangeAllRate() {

        ExchangeRateResponse response = null;
        return  ResponseEntity.ok().body(response);
    }
}
