package hhplus.concert.domain.model;

import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Test
    void 잔액이_사용할_금액과_같거나_많으면_사용에_성공한다() {
        // given
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(10_000L)  // 잔액 10,000원
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        // when
        Point updatedPoint = point.usePoint(5_000);  // 5,000원 사용

        // then
        assertThat(updatedPoint.amount()).isEqualTo(5_000L);  // 남은 잔액 5,000원
        assertThat(updatedPoint.lastUpdatedAt()).isAfter(point.lastUpdatedAt());  // 시간 업데이트 확인
    }

    @Test
    void 잔액이_부족하면_사용에_실패한다() {
        // given
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(3_000L)  // 잔액 3,000원
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        // when / then
        assertThatThrownBy(() -> point.usePoint(5_000))  // 5,000원 사용 시도
                .isInstanceOf(CoreException.class)
                .hasMessage(ErrorType.PAYMENT_FAILED_AMOUNT.getMessage());
    }

}