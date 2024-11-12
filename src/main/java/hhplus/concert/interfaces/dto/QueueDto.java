package hhplus.concert.interfaces.dto;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.support.type.QueueStatus;
import lombok.Builder;

public class QueueDto {
    @Builder
    public record QueueRequest (
            Long userId
    ) {
    }

    @Builder
    public record QueueResponse (
        String token,
        QueueStatus status,
        Long rank
    ) {
        public static QueueResponse of(Queue token) {
            return QueueResponse.builder()
                    .token(token.token())
                    .status(token.status())
                    .rank(token.rank())
                    .build();
        }
    }
}
