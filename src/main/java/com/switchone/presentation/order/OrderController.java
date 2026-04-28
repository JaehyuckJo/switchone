package com.switchone.presentation.order;

import com.switchone.application.order.dto.request.OrderRequest;
import com.switchone.application.order.dto.response.OrderListResponse;
import com.switchone.application.order.dto.response.OrderResponse;
import com.switchone.application.order.facade.OrderFacade;
import com.switchone.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> order(@RequestBody @Valid OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.order(request)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<OrderListResponse>> getOrderList() {
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getOrderList()));
    }
}
