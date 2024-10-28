package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.type.SeatStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Seat(
    Long id,
    Long concertScheduleId,
    int seatNo,
    SeatStatus status,
    LocalDateTime reservationAt,
    int seatPrice
) {
    public void checkStatus() {
        if (status.equals(SeatStatus.UNAVAILABLE)) {
            throw new CoreException(ErrorCode.SEAT_UNAVAILABLE);
        }
    }

    public Seat assign() {
        return Seat.builder()
                .id(this.id)
                .concertScheduleId(this.concertScheduleId)
                .seatNo(this.seatNo)
                .status(SeatStatus.UNAVAILABLE)
                .reservationAt(LocalDateTime.now())
                .seatPrice(this.seatPrice)
                .build();
    }

    public Seat toAvailable() {
        if (this.status == SeatStatus.UNAVAILABLE) {
            return Seat.builder()
                    .id(this.id)
                    .concertScheduleId(this.concertScheduleId)
                    .seatNo(this.seatNo)
                    .status(SeatStatus.AVAILABLE)
                    .reservationAt(LocalDateTime.now())
                    .seatPrice(this.seatPrice)
                    .build();
        }
        return null;
    }
}
