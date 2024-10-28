package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorCode;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Point(
        Long id,
        Long userId,
        Long amount,
        LocalDateTime lastUpdatedAt
) {
    public Point charge(Long amount) {
        return Point.builder()
                .id(this.id)
                .userId(this.userId)
                .amount(this.amount + amount)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    public Point usePoint(int useAmount) {
        if (this.amount < useAmount) {
            throw new CoreException(ErrorCode.PAYMENT_FAILED_AMOUNT);
        }
        return Point.builder()
                .id(id)
                .userId(userId)
                .amount(amount - useAmount)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}
