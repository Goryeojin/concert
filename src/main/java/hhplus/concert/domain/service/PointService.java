package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Point;
import hhplus.concert.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getPoint(Long userId) {
        return pointRepository.findPoint(userId);
    }

    public void usePoint(Point point, int amount) {
        Point usedPoint = point.usePoint(amount);
        pointRepository.save(usedPoint);
    }

    public Point chargePoint(Long userId, Long amount) {
        Point point = pointRepository.findPoint(userId);
        Point updatedPoint = point.charge(amount);
        pointRepository.save(updatedPoint);
        return updatedPoint;
    }
}
