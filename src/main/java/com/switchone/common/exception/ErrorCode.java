package com.switchone.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EXCHANGE_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 통화의 환율 데이터가 없습니다"),
    INVALID_CURRENCY(HttpStatus.BAD_REQUEST, "지원하지 않는 통화 코드입니다"),
    INVALID_CURRENCY_PAIR(HttpStatus.BAD_REQUEST, "KRW와 외화 간의 주문만 가능합니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
