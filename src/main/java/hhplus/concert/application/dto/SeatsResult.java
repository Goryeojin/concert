package hhplus.concert.application.dto;

import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Seat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SeatsResult(
        Long scheduleId,
        Long concertId,
        LocalDateTime concertAt,
        List<Seat> seats
) {
    public static SeatsResult from(ConcertSchedule schedule, List<Seat> seats) {
        return SeatsResult.builder()
                .scheduleId(schedule.id())
                .concertId(schedule.concertId())
                .concertAt(schedule.concertAt())
                .seats(seats)
                .build();
    }
}
