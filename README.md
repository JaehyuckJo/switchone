# SwitchOne

한국수출입은행 Open API를 연동한 실시간 과제입니다.

## Tech Stack

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.5 |
| Database | H2 (In-Memory) |
| ORM | Spring Data JPA / Hibernate |
| Build | Gradle |

## Getting Started

> **실행 전 필수 설정**
> `src/main/resources/application.properties` 에서 아래 값을 메일에 첨부된 인증키로 변경 후 실행해주세요.
> ```properties
> external.koreaexim.auth-key={API_KEY}
> ```

```bash
./gradlew bootRun
```

서버 실행 후 `http://localhost:8080` 에서 접근 가능합니다.

H2 콘솔: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (없음)

## 환율 데이터

서버 시작 시 스케줄러가 동작하며, **60초마다** 한국수출입은행 Open API로부터 환율 데이터를 자동으로 갱신합니다.

지원 통화: `USD`, `JPY`, `EUR`, `CNY`

---


## Error Response

```json
{
  "code": "에러 코드",
  "message": "에러 메시지"
}
```

| 에러 코드 | HTTP Status | 설명 |
|----------|-------------|------|
| `EXCHANGE_RATE_NOT_FOUND` | 404 | 해당 통화의 환율 데이터가 없음 |
| `INVALID_CURRENCY` | 400 | 지원하지 않는 통화 코드 |
| `INVALID_CURRENCY_PAIR` | 400 | KRW와 외화 간의 주문이 아닌 경우 |
| `VALIDATION_FAILED` | 400 | 요청 값 유효성 검사 실패 |

---

## Project Structure

```
src/main/java/com/switchone
├── domain
│   ├── exchange
│   │   ├── entity        # ExchangeRate
│   │   ├── enumtype      # CurrencyCode
│   │   └── repository    # ExchangeRateRepository
│   └── order
│       ├── entity        # Order
│       └── repository    # OrderRepository
├── application
│   ├── exchange
│   │   ├── dto/response  # ExchangeRateResponse, LatestExchangeRateResponse
│   │   └── facade        # ExchangeRateFacade
│   └── order
│       ├── dto/request   # OrderRequest
│       ├── dto/response  # OrderResponse, OrderListResponse
│       └── facade        # OrderFacade
├── presentation
│   ├── exchange          # ExchangeController
│   └── order             # OrderController
├── infrastructure
│   └── scheduler         # ExchangeRateScheduler
└── common
    ├── config            # RestClientConfig
    ├── exception         # BusinessException, ErrorCode, GlobalExceptionHandler
    └── response          # ApiResponse, ErrorResponse
```
