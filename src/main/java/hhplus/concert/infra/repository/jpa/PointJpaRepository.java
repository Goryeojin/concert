package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.PointEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<PointEntity> findByUserId(Long userId);
}
