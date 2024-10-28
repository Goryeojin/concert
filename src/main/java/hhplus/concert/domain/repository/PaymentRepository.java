package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);

    // 테스트용
    List<Payment> findByUserId(Long userId);
}
