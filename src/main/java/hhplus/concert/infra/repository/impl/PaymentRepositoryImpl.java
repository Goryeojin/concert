package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Payment;
import hhplus.concert.domain.repository.PaymentRepository;
import hhplus.concert.infra.entity.PaymentEntity;
import hhplus.concert.infra.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentJpaRepository.save(PaymentEntity.from(payment));
        return entity.of();
    }
}
