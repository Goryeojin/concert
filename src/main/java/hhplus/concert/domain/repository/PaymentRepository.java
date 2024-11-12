package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
