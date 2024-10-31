package hhplus.concert.application.facade;

import hhplus.concert.domain.model.Point;
import hhplus.concert.domain.service.PointService;
import hhplus.concert.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;

    public Point getPoint(Long userId) {
        userService.existsUser(userId);
        return pointService.getPoint(userId);
    }

    public Point chargePoint(Long userId, Long amount) {
        userService.existsUser(userId);
        return pointService.chargePoint(userId, amount);
    }
}
