package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.infra.entity.QueueEntity;
import hhplus.concert.infra.repository.jpa.QueueJpaRepository;
import hhplus.concert.support.exception.CustomException;
import hhplus.concert.support.exception.ErrorCode;
import hhplus.concert.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Queue findQueue(Long userId) {
        return queueJpaRepository.findByUserIdAndStatusNot(userId, QueueStatus.EXPIRED)
                .map(QueueEntity::of)
                .orElse(null);
    }

    @Override
    public Queue findQueue(String token) {
        return queueJpaRepository.findByToken(token)
                .map(QueueEntity::of)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Long findActiveCount() {
        return queueJpaRepository.countByStatus(QueueStatus.ACTIVE);
    }

    @Override
    public Long findCurrentRank() {
        return queueJpaRepository.countByStatus(QueueStatus.WAITING);
    }

    @Override
    public Long findUserRank(Long queueId) {
        return queueJpaRepository.countByIdLessThanAndStatus(queueId, QueueStatus.WAITING) + 1;
    }

    @Override
    public Queue save(Queue token) {
        return QueueEntity.of(queueJpaRepository.save(new QueueEntity().from(token)));
    }

    @Override
    public void expireToken(Queue token) {
        queueJpaRepository.updateStatusAndExpiredAtById(token.id(), token.status(), token.expiredAt());
    }
}