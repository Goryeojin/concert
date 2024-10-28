package hhplus.concert.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hhplus.concert.domain.model.Queue;
import hhplus.concert.support.type.QueueStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class QueueDto {
    @Builder
    public record QueueRequest (
            Long userId
    ) {
    }

    @Builder
    public record QueueResponse (
        String token,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime enteredAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiredAt,
        QueueStatus status,
        Long rank
    ) {
        public static QueueResponse of(Queue token) {
            return QueueResponse.builder()
                    .token(token.token())
                    .status(token.status())
                    .createdAt(token.createdAt())
                    .rank(token.rank())
                    .build();
        }

        public static QueueResponse statusOf(Queue queue) {
            return QueueResponse.builder()
                    .status(queue.status())
                    .rank(queue.rank())
                    .enteredAt(queue.enteredAt())
                    .expiredAt(queue.expiredAt())
                    .build();
        }
    }
}
