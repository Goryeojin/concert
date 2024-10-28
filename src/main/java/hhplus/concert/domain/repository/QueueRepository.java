package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.support.type.QueueStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueRepository {

    Queue findQueue(Long userId);
    Queue findQueue(String token);
    Long findByStatus(QueueStatus queueStatus);
    Long findUserRank(Long queueId);
    Queue save(Queue token);
    void expireToken(Queue expiredToken);
    List<Queue> findExpiredTokens(LocalDateTime now, QueueStatus queueStatus);
    List<Queue> findWaitingTokens(long neededTokens);
}
