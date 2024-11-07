package hhplus.concert.application.facade;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.service.QueueService;
import hhplus.concert.domain.service.UserService;
import hhplus.concert.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final UserService userService;
    private final QueueService queueService;

    @Transactional
    @DistributedLock(key = "#lockName")
    public Queue issueToken(String lockName, Long userId) {
        // 유저 유무 확인
        userService.validateUser(userId);
        // 토큰 발급
        return queueService.issueToken(userId);
    }

    // 대기열 상태 조회
    @Cacheable(value = "queueStatus", key = "#token", cacheManager = "caffeineCacheManager")
    public Queue status(String token, Long userId) {
        userService.validateUser(userId);
        // 토큰 조회
        return queueService.getToken(token);
    }
}
