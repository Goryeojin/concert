package hhplus.concert.interfaces.dto;

import hhplus.concert.support.type.ConcertStatus;
import lombok.Builder;

@Builder
public record ConcertDto(
        Long concertId,
        String title,
        String description,
        ConcertStatus status
) {
}
