package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.exception.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private static final long MAX_ACTIVE_TOKENS = 200;

    public Queue issueToken(Long userId) {
        // 활성화 상태 토큰 개수 검색
        Long activeCount = queueRepository.getActiveTokenCount();
        // 대기 순번 조회
        Long rank = queueRepository.getWaitingTokenCount();
        // 토큰 생성
        Queue token = Queue.createToken(userId, activeCount, rank);
        // 토큰 저장
        System.out.println(token);
        if (token.checkStatus()) {
            queueRepository.saveActiveToken(token.token());
        } else {
            queueRepository.saveWaitingToken(token.token());
        }
        return token;
    }

    public void expireToken(String token) {
        queueRepository.removeToken(token);
    }

    public void validateToken(String token) {
        boolean exists = queueRepository.activeTokenExist(token);
        if (!exists) throw new CoreException(ErrorType.TOKEN_INVALID, "유효하지 않은 토큰입니다.");
    }

    public Queue getToken(String token) {
        return queueRepository.findToken(token);
    }

    public void updateActiveTokens() {
        long activeCount = queueRepository.getActiveTokenCount();
        if (activeCount < MAX_ACTIVE_TOKENS) {
            long neededTokens = MAX_ACTIVE_TOKENS - activeCount;
            List<String> waitingTokens = queueRepository.retrieveAndRemoveWaitingTokens(neededTokens);

            waitingTokens.forEach(queueRepository::saveActiveToken);
        }
    }
}
