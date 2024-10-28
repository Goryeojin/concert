package hhplus.concert.application.facade;

import hhplus.concert.application.dto.ReservationCommand;
import hhplus.concert.application.dto.ReservationResult;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.service.ConcertService;
import hhplus.concert.domain.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ConcertService concertService;
    private final ReservationService reservationService;

    @Transactional
    public ReservationResult reservation(ReservationCommand command) {
        // 콘서트 상태 조회
        ConcertSchedule schedule = concertService.scheduleInfo(command.scheduleId());
        Seat seat = concertService.getSeat(command.seatId());
        // 예약 가능 상태인지 확인
        concertService.isAvailableReservation(schedule, seat);
        // 예약 정보 저장
        Reservation reservation = reservationService.reservation(schedule, seat, command.userId());
        // 좌석 점유
        concertService.assignmentSeat(seat);
        // 예약 정보 리턴
        return ReservationResult.builder()
                .reservationId(reservation.id())
                .concertId(schedule.concertId())
                .concertAt(schedule.concertAt())
                .seat(Seat.builder().id(seat.id()).seatNo(seat.seatNo()).seatPrice(seat.seatPrice()).build())
                .status(reservation.status())
                .build();
    }
}
