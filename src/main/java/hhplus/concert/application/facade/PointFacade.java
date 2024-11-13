package hhplus.concert.application.facade;

import hhplus.concert.domain.model.Point;
import hhplus.concert.domain.service.PointService;
import hhplus.concert.domain.service.UserService;
import hhplus.concert.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;

    @Cacheable(value = "point", key = "#userId", cacheManager = "caffeineCacheManager")
    public Point getPoint(Long userId) {
        userService.validateUser(userId);
        return pointService.getPoint(userId);
    }

    @DistributedLock(key = "#lockName")
    public Point chargePoint(String lockName, Long userId, Long amount) {
        userService.validateUser(userId);
        return pointService.chargePoint(userId, amount);
    }
}
