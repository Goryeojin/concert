package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PointEntity> findByUserId(Long userId);
}
