package hhplus.concert.application.dto;

import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.support.type.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationResult(

        Long reservationId,
        Long concertId,
        LocalDateTime concertAt,
        Seat seat,
        ReservationStatus status
) {

    public static ReservationResult from(Reservation reservation, ConcertSchedule schedule, Seat seat) {
        return ReservationResult.builder()
                .reservationId(reservation.id())
                .concertId(schedule.concertId())
                .concertAt(schedule.concertAt())
                .seat(Seat.builder().id(seat.id()).seatNo(seat.seatNo()).seatPrice(seat.seatPrice()).build())
                .status(reservation.status())
                .build();
    }
}
