package hhplus.concert.interfaces.controller;

import hhplus.concert.application.facade.QueueFacade;
import hhplus.concert.domain.model.Queue;
import hhplus.concert.interfaces.dto.QueueDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    // 대기열 등록, 토큰 발급
    @PostMapping("/tokens")
    public ResponseEntity<QueueDto.QueueResponse> createToken(@Valid @RequestBody QueueDto.QueueRequest request) {
        Queue token = queueFacade.createToken(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QueueDto.QueueResponse.of(token));
    }

    // 대기열 상태 조회
    @GetMapping("/status")
    public ResponseEntity<QueueDto.QueueResponse> getStatus(
            @RequestHeader("Token") @NotBlank String token,
            @RequestHeader("User-Id") Long userId
    ) {
        Queue queue = queueFacade.getStatus(token, userId);
        return ok(QueueDto.QueueResponse.statusOf(queue));
    }
}
