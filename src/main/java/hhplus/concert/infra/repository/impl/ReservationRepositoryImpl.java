package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.repository.ReservationRepository;
import hhplus.concert.infra.entity.ReservationEntity;
import hhplus.concert.infra.repository.jpa.ReservationJpaRepository;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(ReservationEntity.from(reservation)).of();
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationJpaRepository.findByReservationId(reservationId)
                .map(ReservationEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "예약 ID: " + reservationId));
    }

    @Override
    public Reservation findByIdWithoutLock(Long reservationId) {
        return reservationJpaRepository.findByReservationIdWithoutLock(reservationId)
                .map(ReservationEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "예약 ID: " + reservationId));
    }

    @Override
    public List<Reservation> findByConcertIdAndScheduleIdAndSeatId(Long concertId, Long scheduleId, Long seatId) {
        return reservationJpaRepository.findByConcertIdAndScheduleIdAndSeatId(concertId, scheduleId, seatId).stream()
                .map(ReservationEntity::of)
                .toList();
    }

    @Override
    public List<Reservation> findExpiredReservation(ReservationStatus reservationStatus, LocalDateTime localDateTime) {
        return reservationJpaRepository.findByStatusAndReservationAtBefore(reservationStatus, localDateTime).stream()
                .map(ReservationEntity::of)
                .toList();
    }
}
