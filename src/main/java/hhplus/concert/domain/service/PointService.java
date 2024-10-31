package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Point;
import hhplus.concert.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public Point getPoint(Long userId) {
        return pointRepository.findPoint(userId);
    }

    @Transactional
    public Point chargePoint(Long userId, Long amount) {
        Point point = pointRepository.findPoint(userId);
        Point updatedPoint = point.charge(amount);
        pointRepository.save(updatedPoint);
        return updatedPoint;
    }

    public void usePoint(Point point, int amount) {
        Point usedPoint = point.usePoint(amount);
        pointRepository.save(usedPoint);
    }
}
