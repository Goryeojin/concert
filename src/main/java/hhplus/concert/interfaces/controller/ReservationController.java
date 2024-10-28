package hhplus.concert.interfaces.controller;

import hhplus.concert.application.dto.ReservationResult;
import hhplus.concert.application.facade.ReservationFacade;
import hhplus.concert.interfaces.dto.ReservationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFacade reservationFacade;

    /**
     * 콘서트 좌석을 예약한다.
     * @param token 발급받은 토큰
     * @param request userId, concertId, scheduleId, seats
     * @return 예약 결과 dto
     */
    @PostMapping
    public ResponseEntity<ReservationDto.ReservationResponse> createReservation(
            @RequestHeader("Token") String token,
            @Valid @RequestBody ReservationDto.ReservationRequest request
    ) {
        ReservationResult reservation = reservationFacade.reservation(request.toCommand(token));
        return ResponseEntity.ok()
                .body(ReservationDto.ReservationResponse.of(reservation));
    }
}
