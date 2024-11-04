package hhplus.concert.domain.service;

import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.repository.ReservationRepository;
import hhplus.concert.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation reservation(ConcertSchedule schedule, Seat seat, Long userId) {
        // 예약 정보를 생성한다.
        Reservation reservation = Reservation.create(schedule, seat.id(), userId);// 예약 생성
        // 예약 정보를 저장한다.
        return reservationRepository.save(reservation);
    }

    public Reservation checkReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        // 예약 정보를 확인한다.
        reservation.checkValidation(userId);
        return reservation;
    }

    public Reservation checkReservationWithoutLock(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdWithoutLock(reservationId);
        // 예약 정보를 확인한다.
        reservation.checkValidation(userId);
        return reservation;
    }

    public Reservation changeStatus(Reservation reservation, ReservationStatus status) {
        Reservation changedReservation = reservation.changeStatus(status);
        return reservationRepository.save(changedReservation);
    }
}
