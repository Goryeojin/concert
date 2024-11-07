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

    @Cacheable(value = "concert", key = "'concert'", cacheManager = "redisCacheManager")
    public List<Concert> getConcerts() {
        return concertService.getConcerts();
    }

    @Cacheable(value = "schedule", key = "#concertId", cacheManager = "redisCacheManager")
    public List<ConcertSchedule> getConcertSchedules(Long concertId) {
        Concert concert = concertService.getConcert(concertId);
        return concertService.getConcertSchedules(concert);
    }

    @Cacheable(value = "shortLivedCache", key = "#scheduleId", cacheManager = "redisCacheManager")
    public SeatsResult getSeats(Long concertId, Long scheduleId) {
        Concert concert = concertService.getConcert(concertId);
        ConcertSchedule schedule = concertService.scheduleInfo(scheduleId);
        List<Seat> seats = concertService.getSeats(concert.id(), schedule.id(), SeatStatus.AVAILABLE);

        return SeatsResult.from(schedule, seats);
    }
}
