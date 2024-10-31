package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Point;

public interface PointRepository {
    Point findPoint(Long userId);

    void save(Point updatedPoint);

    Point findPointWithoutLock(Long userId);
}
