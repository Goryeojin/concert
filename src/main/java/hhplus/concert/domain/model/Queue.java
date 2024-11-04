package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.type.QueueStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Queue (
        Long id,
        Long userId,
        String token,
        QueueStatus status,
        LocalDateTime createdAt,
        LocalDateTime enteredAt,
        LocalDateTime expiredAt,
        Long rank
) {
    public static Queue createToken(Long userId, Long activeCount, Long rank) {
        // 활성 토큰이 50개 미만이고, 대기 순번이 0이면 `ACTIVE`
        // 활성 토큰이 50개 이상이거나, 대기 순번이 1이상이면 `WAITING`
        QueueStatus status = (rank == 0 && activeCount < 50) ? QueueStatus.ACTIVE : QueueStatus.WAITING;
        LocalDateTime now = LocalDateTime.now();

        String userData = userId + now.toString();
        String token = UUID.nameUUIDFromBytes(userData.getBytes()).toString();

        return Queue.builder()
                .userId(userId)
                .token(token)
                .rank((status == QueueStatus.WAITING) ? rank + 1 : 0)
                .status(status)
                .createdAt(now)
                .enteredAt((status == QueueStatus.ACTIVE) ? now : null)
                .expiredAt((status == QueueStatus.ACTIVE) ? now.plusMinutes(10) : null)
                .build();
    }

    public Queue expired() {
        return Queue.builder()
                .id(this.id)
                .userId(this.userId)
                .token(this.token)
                .createdAt(this.createdAt)
                .enteredAt(this.enteredAt)
                .status(QueueStatus.EXPIRED)
                .expiredAt(LocalDateTime.now())
                .build();
    }

    public boolean checkStatus() {
        if (status.equals(QueueStatus.EXPIRED)) {
            throw new CoreException(ErrorType.TOKEN_INVALID, "토큰 상태: " + status);
        }
        return status.equals(QueueStatus.ACTIVE);
    }

    public void validateToken() {
        // 토큰이 대기 상태이거나 만료되었을 경우
        if (expiredAt == null || expiredAt.isBefore(LocalDateTime.now())) {
            throw new CoreException(ErrorType.TOKEN_INVALID, "토큰 상태: " + status);
        }
    }

    public Queue activate() {
        return Queue.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .status(QueueStatus.ACTIVE)
                .enteredAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }
}
