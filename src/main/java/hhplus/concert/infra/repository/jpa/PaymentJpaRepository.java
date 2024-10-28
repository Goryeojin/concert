package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findAllByUserId(Long userId);
}
