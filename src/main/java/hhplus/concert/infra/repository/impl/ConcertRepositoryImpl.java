package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Concert;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.repository.ConcertRepository;
import hhplus.concert.infra.entity.ConcertEntity;
import hhplus.concert.infra.entity.ConcertScheduleEntity;
import hhplus.concert.infra.entity.SeatEntity;
import hhplus.concert.infra.repository.jpa.ConcertJpaRepository;
import hhplus.concert.infra.repository.jpa.ConcertScheduleJpaRepository;
import hhplus.concert.infra.repository.jpa.SeatJpaRepository;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Concert> findConcerts() {
        return concertJpaRepository.findAll().stream()
                .map(ConcertEntity::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcertSchedule> findConcertSchedules(Long concertId) {
        LocalDateTime now = LocalDateTime.now();
        return concertScheduleJpaRepository.findByConcertIdAndReservationAtBeforeAndDeadlineAfter(concertId, now, now).stream()
                        .map(ConcertScheduleEntity::of)
                        .toList();
    }

    @Override
    public Concert findConcert(Long concertId) {
        return concertJpaRepository.findById(concertId)
                .map(ConcertEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "검색한 콘서트 ID: " + concertId));
    }

    @Override
    public List<Seat> findSeats(Long concertId, Long scheduleId, SeatStatus seatStatus) {
        return seatJpaRepository.findSeats(concertId, scheduleId, seatStatus).stream()
                .map(SeatEntity::of)
                .toList();
    }

    @Override
    public ConcertSchedule findConcertSchedule(Long scheduleId) {
        return concertScheduleJpaRepository.findById(scheduleId)
                .map(ConcertScheduleEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "검색한 일정 ID: " + scheduleId));
    }

    @Override
    public void saveSeat(Seat seat) {
        seatJpaRepository.save(SeatEntity.from(seat));
    }

    @Override
    @Transactional
    public Seat findSeat(Long seatId) {
        return seatJpaRepository.findBySeatId(seatId)
                .map(SeatEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "검색한 좌석 ID: " + seatId));
    }

    @Override
    public Seat findById(Long seatId) {
        return seatJpaRepository.findById(seatId)
                .map(SeatEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "검색한 좌석 ID: " + seatId));
    }
}
