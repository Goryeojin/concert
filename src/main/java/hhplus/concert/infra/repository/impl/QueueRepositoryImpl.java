package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.infra.entity.QueueEntity;
import hhplus.concert.infra.repository.jpa.QueueJpaRepository;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
                .orElseThrow(() -> new CoreException(ErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Long findByStatus(QueueStatus status) {
        return queueJpaRepository.countByStatus(status);
    }

    @Override
    public Long findUserRank(Long queueId) {
        return queueJpaRepository.countByIdLessThanAndStatus(queueId, QueueStatus.WAITING) + 1;
    }

    @Override
    public Queue save(Queue token) {
        return queueJpaRepository.save(new QueueEntity().from(token)).of();
    }

    @Override
    public void expireToken(Queue token) {
        queueJpaRepository.updateStatusAndExpiredAtById(token.id(), token.status(), token.expiredAt());
    }

    @Override
    public List<Queue> findExpiredTokens(LocalDateTime now, QueueStatus queueStatus) {
        return queueJpaRepository.findExpiredTokens(now, queueStatus).stream()
                .map(QueueEntity::of)
                .toList();
    }

    @Override
    public List<Queue> findWaitingTokens(long limit) {
        Pageable pageable = PageRequest.of(0, (int) limit);
        return queueJpaRepository.findWaitingTokens(pageable).getContent().stream()
                .map(QueueEntity::of)
                .toList();
    }
}
