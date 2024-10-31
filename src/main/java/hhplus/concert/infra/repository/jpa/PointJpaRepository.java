package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.PointEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PointEntity> findByUserId(Long userId);

    @Query("select p from point p where p.user.id = ?1")
    Optional<PointEntity> findByUserIdWithoutLock(Long userId);
}
