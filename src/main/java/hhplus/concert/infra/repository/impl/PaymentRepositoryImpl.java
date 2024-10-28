package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Payment;
import hhplus.concert.domain.repository.PaymentRepository;
import hhplus.concert.infra.entity.PaymentEntity;
import hhplus.concert.infra.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentJpaRepository.save(PaymentEntity.from(payment));
        return entity.of();
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return paymentJpaRepository.findAllByUserId(userId).stream()
                .map(PaymentEntity::of)
                .toList();
    }
}
