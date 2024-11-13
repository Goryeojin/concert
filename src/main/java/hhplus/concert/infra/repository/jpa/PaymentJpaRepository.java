package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
}
