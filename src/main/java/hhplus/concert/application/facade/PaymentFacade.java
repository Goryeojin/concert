package hhplus.concert.application.facade;

import hhplus.concert.domain.model.*;
import hhplus.concert.domain.service.*;
import hhplus.concert.support.aop.DistributedLock;
import hhplus.concert.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final ConcertService concertService;

    @Transactional
    public Payment payment(String token, Long reservationId, Long userId) {
        // 예약 검증 (본인인지, 시간 오버 안됐는지)
        Reservation reservation = reservationService.checkReservation(reservationId, userId);
        Seat seat = concertService.getSeat(reservation.seatId());
        Point point = pointService.getPoint(userId);
        // 잔액을 변경한다.
        pointService.usePoint(point, seat.seatPrice());
        // 예약 상태를 변경한다.
        Reservation reserved = reservationService.changeStatus(reservation, ReservationStatus.COMPLETED);
        // 결제 완료 시 토큰을 만료로 처리한다.
        Queue queue = queueService.getToken(token);
        queueService.expireToken(queue);
        // 결제 내역을 생성한다.
        return paymentService.createBill(reserved.id(), userId, seat.seatPrice());
    }

    @DistributedLock(key = "#lockName")
    public Payment payment(String lockName, String token, Long reservationId, Long userId) {
        // 예약 검증 (본인인지, 시간 오버 안됐는지)
        Reservation reservation = reservationService.checkReservationWithoutLock(reservationId, userId);
        Seat seat = concertService.getSeatWithoutLock(reservation.seatId());
        Point point = pointService.getPointWithoutLock(userId);
        // 잔액을 변경한다.
        pointService.usePoint(point, seat.seatPrice());
        // 예약 상태를 변경한다.
        Reservation reserved = reservationService.changeStatus(reservation, ReservationStatus.COMPLETED);
        // 결제 완료 시 토큰을 만료로 처리한다.
        Queue queue = queueService.getToken(token);
        queueService.expireToken(queue);
        // 결제 내역을 생성한다.
        return paymentService.createBill(reserved.id(), userId, seat.seatPrice());
    }
}
