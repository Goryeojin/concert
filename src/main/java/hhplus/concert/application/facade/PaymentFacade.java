package hhplus.concert.application.facade;

import hhplus.concert.domain.dto.PaymentEventCommand;
import hhplus.concert.domain.model.Payment;
import hhplus.concert.domain.model.Point;
import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.service.*;
import hhplus.concert.support.aop.DistributedLock;
import hhplus.concert.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final ConcertService concertService;
    private final PaymentEventService paymentEventService;

    /**
     * 결제 진행
     * @param lockName 포인트 충전 요청과 충돌 가능성이 있으므로 같은 lockName 을 사용하도록 한다.
     */
    @DistributedLock(key = "#lockName")
    public Payment processPayment(String lockName, String token, Long reservationId, Long userId) {
        /* 1. 도메인 로직 */
        // 예약 검증 (본인인지, 시간 오버 안됐는지)
        Reservation reservation = reservationService.checkReservation(reservationId, userId);
        Seat seat = concertService.getSeat(reservation.seatId());
        Point point = pointService.getPoint(userId);
        // 잔액을 변경한다.
        pointService.usePoint(point, seat.seatPrice());
        // 예약 상태를 변경한다.
        Reservation reserved = reservationService.changeStatus(reservation, ReservationStatus.COMPLETED);
        // 결제 완료 시 토큰을 만료로 처리한다.
        queueService.expireToken(token);
        // 결제 내역을 생성한다.
        Payment bill = paymentService.createBill(reserved.id(), userId, seat.seatPrice());

        /* 2. 이벤트 발행 */
        paymentEventService.publishEvent(PaymentEventCommand.from(bill));
        return bill;
    }
}
