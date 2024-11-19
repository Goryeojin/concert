package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.infra.repository.RedisRepository;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final RedisRepository redisRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVE_TOKEN_KEY = "activeToken";
    private static final String WAITING_TOKEN_KEY = "waitingToken";
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    @Override
    public boolean activeTokenExist(String token) {
        return redisRepository.keyExists(ACTIVE_TOKEN_KEY + ":" + token);
    }

    @Override
    public Long getActiveTokenCount() {
        // 활성 상태의 토큰 개수 반환
        return redisRepository.getSize(ACTIVE_TOKEN_KEY);
    }

    @Override
    public Long getWaitingTokenCount() {
        // 대기 상태의 토큰 개수 반환
        return redisRepository.getSortedSetSize(WAITING_TOKEN_KEY);
    }

    @Override
    public void saveActiveToken(Object token) {
        // 활성 상태 토큰 추가
        redisRepository.put(ACTIVE_TOKEN_KEY + ":" + token, token, TOKEN_TTL);
    }

    @Override
    public void saveWaitingToken(String token) {
        // 대기 상태의 토큰 추가
        redisRepository.addSortedSet(WAITING_TOKEN_KEY, token, System.currentTimeMillis());
    }

    @Override
    public void removeToken(String token) {
        // 활성 상태의 특정 토큰 제거
        redisRepository.remove(ACTIVE_TOKEN_KEY + ":" + token);
    }

    @Override
    public List<Object> getWaitingTokens(long count) {
        // 비어있는 활성 토큰 수만큼 대기열에서 토큰을 가져온다.
        Set<Object> tokens = redisRepository.getSortedSetRange(WAITING_TOKEN_KEY, 0, count - 1);

        if (tokens != null && !tokens.isEmpty()) {
            return tokens.stream().toList();
        }
        return List.of();
    }

    @Override
    public Queue findToken(String token) {
        // 활성 토큰 유무 확인
        Object activeToken = redisRepository.get(ACTIVE_TOKEN_KEY + ":" + token);
        if (activeToken != null) {
            return Queue.builder().token(token).status(QueueStatus.ACTIVE).build();
        }

        // 대기열 유무 확인
        Long waitingRank = redisRepository.getSortedSetRank(WAITING_TOKEN_KEY, token);
        if (waitingRank != null) {
            return Queue.builder().token(token).status(QueueStatus.WAITING).rank(waitingRank).build();
        }

        // 없다면 에러 반환
        throw new CoreException(ErrorType.RESOURCE_NOT_FOUND, "토큰: " + token);
    }

    @Override
    public void removeWaitingToken(Set<Object> tokens) {
        redisRepository.removeSortedSetMembers(WAITING_TOKEN_KEY, tokens);
    }
}
