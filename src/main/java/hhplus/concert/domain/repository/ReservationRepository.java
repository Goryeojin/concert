package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Reservation;
import hhplus.concert.support.type.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Reservation findById(Long reservationId);

    Reservation findByIdWithoutLock(Long reservationId);

    List<Reservation> findByConcertIdAndScheduleIdAndSeatId(Long concertId, Long scheduleId, Long seatId);

    List<Reservation> findExpiredReservation(ReservationStatus reservationStatus, LocalDateTime localDateTime);
}
