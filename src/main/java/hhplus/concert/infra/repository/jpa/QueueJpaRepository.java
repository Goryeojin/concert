package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.QueueEntity;
import hhplus.concert.support.type.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<QueueEntity, Long> {
    Optional<QueueEntity> findByUserIdAndStatusNot(Long userId, QueueStatus status);
    Optional<QueueEntity> findByToken(String token);
    Long countByIdLessThanAndStatus(Long id, QueueStatus statuses);

    @Modifying
    @Query("UPDATE queue q SET q.status = :status, q.expiredAt = :expiredAt WHERE q.id = :id")
    void updateStatusAndExpiredAtById(@Param("id") Long id,
                                      @Param("status") QueueStatus status,
                                      @Param("expiredAt") LocalDateTime expiredAt);

    Long countByStatus(QueueStatus queueStatus);
}