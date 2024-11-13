package hhplus.concert.application.facade;

import hhplus.concert.application.dto.SeatsResult;
import hhplus.concert.domain.model.Concert;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.service.ConcertService;
import hhplus.concert.support.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    /**
     * 콘서트 목록 조회
     */
    @Cacheable(value = "concert", key = "'all'", cacheManager = "redisCacheManager")
    public List<Concert> getConcerts() {
        return concertService.getConcerts();
    }

    /**
     * 예약 가능한 일정 조회
     */
    @Cacheable(value = "schedule", key = "#concertId", cacheManager = "redisCacheManager")
    public List<ConcertSchedule> getConcertSchedules(Long concertId) {
        Concert concert = concertService.getConcert(concertId);
        return concertService.getConcertSchedules(concert);
    }

    /**
     * 예약 가능한 좌석 조회
     */
    @Cacheable(value = "shortLivedCache", key = "#scheduleId", cacheManager = "redisCacheManager")
    public SeatsResult getSeats(Long concertId, Long scheduleId) {
        Concert concert = concertService.getConcert(concertId);
        ConcertSchedule schedule = concertService.getSchedule(scheduleId);
        List<Seat> seats = concertService.getSeats(concert.id(), schedule.id(), SeatStatus.AVAILABLE);

        return SeatsResult.from(schedule, seats);
    }
}
