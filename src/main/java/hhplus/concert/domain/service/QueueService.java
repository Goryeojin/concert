package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    public Queue getToken(Long userId) {
        return queueRepository.findQueue(userId);
    }

    public Queue getToken(String token) {
        return queueRepository.findQueue(token);
    }

    public Queue createToken(Long userId) {
        // 활성화 상태 토큰 개수 검색
        Long activeCount = queueRepository.findByStatus(QueueStatus.ACTIVE);
        // 대기 순번 조회
        Long rank = queueRepository.findByStatus(QueueStatus.WAITING);
        // 토큰 생성
        Queue token = Queue.createToken(userId, activeCount, rank);
        // 토큰 저장
        queueRepository.save(token);
        return token;
    }

    public void expireToken(Queue token) {
        Queue expiredToken = token.expired();
        queueRepository.expireToken(expiredToken);
    }

    public Queue checkQueueStatus(Queue queue) {
        // 대기열 상태를 검증한다. 이때 만료된 토큰 사용 시 401 에러 반환
        boolean activated = queue.checkStatus();
        // 활성 상태라면 바로 반환한다.
        if (activated) return queue;
        // 대기 중이라면 대기자 수를 조회한다.
        Long rank = queueRepository.findUserRank(queue.id());

        return Queue.builder()
                .status(queue.status())
                .rank(rank)
                .enteredAt(queue.enteredAt())
                .expiredAt(queue.expiredAt())
                .build();
    }

    public Queue validateToken(String token) {
        Queue queue = queueRepository.findQueue(token);
        queue.validateToken();
        return queue;
    }
}
