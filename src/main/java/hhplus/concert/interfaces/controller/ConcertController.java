package hhplus.concert.interfaces.controller;

import hhplus.concert.application.dto.SeatsResult;
import hhplus.concert.application.facade.ConcertFacade;
import hhplus.concert.domain.model.Concert;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.interfaces.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
@RequestHeader("Token") String token
 */
@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 목록을 조회한다.
     */
    @GetMapping
    public ResponseEntity<GetConcertDto.ConcertResponse> getConcerts() {
        List<Concert> concerts = concertFacade.getConcerts();
        return ResponseEntity.ok()
                .body(GetConcertDto.ConcertResponse.of(concerts));
    }

    /**
     * 특정 콘서트의 예약 가능한 일정을 조회한다.
     */
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<GetScheduleDto.ScheduleResponse> getConcertSchedules(
            @PathVariable Long concertId
    ) {
        List<ConcertSchedule> schedules = concertFacade.getConcertSchedules(concertId);
        return ResponseEntity.ok()
                .body(GetScheduleDto.ScheduleResponse.of(concertId, schedules));
    }

    /**
     * 특정 콘서트 일정의 예약 가능한 좌석을 조회한다.
     */
    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    public ResponseEntity<GetSeatDto.SeatResponse> getSeats(
            @PathVariable Long concertId,
            @PathVariable Long scheduleId
    ) {
        SeatsResult seats = concertFacade.getSeats(concertId, scheduleId);
        return ResponseEntity.ok()
                .body(GetSeatDto.SeatResponse.of(seats));
    }
}
