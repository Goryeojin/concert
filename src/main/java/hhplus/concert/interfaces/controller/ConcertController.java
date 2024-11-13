package hhplus.concert.interfaces.controller;

import hhplus.concert.application.dto.SeatsResult;
import hhplus.concert.application.facade.ConcertFacade;
import hhplus.concert.domain.model.Concert;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.interfaces.dto.GetConcertDto;
import hhplus.concert.interfaces.dto.GetScheduleDto;
import hhplus.concert.interfaces.dto.GetSeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 목록 조회
     */
    @GetMapping
    public ResponseEntity<GetConcertDto.ConcertResponse> getConcerts() {
        List<Concert> concerts = concertFacade.getConcerts();
        return ok(GetConcertDto.ConcertResponse.of(concerts));
    }

    /**
     * 예약 가능한 일정 조회
     */
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<GetScheduleDto.ScheduleResponse> getConcertSchedules(
            @PathVariable Long concertId
    ) {
        List<ConcertSchedule> schedules = concertFacade.getConcertSchedules(concertId);
        return ok(GetScheduleDto.ScheduleResponse.of(concertId, schedules));
    }

    /**
     * 예약 가능한 좌석 조회
     */
    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    public ResponseEntity<GetSeatDto.SeatResponse> getSeats(
            @PathVariable Long concertId,
            @PathVariable Long scheduleId
    ) {
        SeatsResult seats = concertFacade.getSeats(concertId, scheduleId);
        return ok(GetSeatDto.SeatResponse.of(seats));
    }
}
