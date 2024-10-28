package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.type.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record Reservation(
        Long id,
        Long concertId,
        Long scheduleId,
        Long seatId,
        Long userId,
        ReservationStatus status,
        LocalDateTime reservationAt
) {
    public static Reservation create(ConcertSchedule schedule, Long seatId, Long userId) {
        return Reservation.builder()
                .concertId(schedule.concertId())
                .scheduleId(schedule.id())
                .seatId(seatId)
                .userId(userId)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservationAt(LocalDateTime.now())
                .build();
    }

    public void checkValidation(Long userId) {
        // 이미 결제되었다면 결제 실패
        if (status == ReservationStatus.COMPLETED) {
            throw new CoreException(ErrorCode.ALREADY_PAID);
        }
        // 예약하고 5분 안에 결제 신청했는지 확인
        if (reservationAt.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new CoreException(ErrorCode.PAYMENT_TIMEOUT);
        }
        // 예약자와 결제자가 같은지 확인
        if (!Objects.equals(userId, userId())) {
            throw new CoreException(ErrorCode.PAYMENT_DIFFERENT_USER);
        }
    }

    public Reservation changeStatus(ReservationStatus status) {
        return Reservation.builder()
                .id(id)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .userId(userId)
                .status(status)
                .reservationAt(this.reservationAt)
                .build();
    }
}
