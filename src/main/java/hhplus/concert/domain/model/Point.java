package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Point(
        Long id,
        Long userId,
        Long amount,
        LocalDateTime lastUpdatedAt,
        Long version
) {
    public Point charge(Long amount) {
        return Point.builder()
                .id(this.id)
                .userId(this.userId)
                .amount(this.amount + amount)
                .lastUpdatedAt(LocalDateTime.now())
                .version(0L)
                .build();
    }

    public Point usePoint(int useAmount) {
        if (this.amount < useAmount) {
            throw new CoreException(ErrorType.PAYMENT_FAILED_AMOUNT, "잔액: " + amount + ", 결제 금액: " + useAmount);
        }
        return Point.builder()
                .id(id)
                .userId(userId)
                .amount(amount - useAmount)
                .lastUpdatedAt(LocalDateTime.now())
                .version(version)
                .build();
    }
}
