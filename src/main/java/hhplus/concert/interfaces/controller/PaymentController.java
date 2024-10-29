package hhplus.concert.interfaces.controller;

import hhplus.concert.application.facade.PaymentFacade;
import hhplus.concert.domain.model.Payment;
import hhplus.concert.interfaces.dto.PaymentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    // 결제 요청
    @PostMapping
    public ResponseEntity<PaymentDto.PaymentResponse> proceedPayment(
            @RequestHeader("Token") String token,
            @Valid @RequestBody PaymentDto.PaymentRequest request
    ) {
        Payment payment = paymentFacade.payment(token, request.reservationId(), request.userId());
        return ok(PaymentDto.PaymentResponse.of(payment));
    }
}
