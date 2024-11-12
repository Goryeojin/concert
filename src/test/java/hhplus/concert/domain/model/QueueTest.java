package hhplus.concert.domain.model;

import hhplus.concert.support.type.QueueStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class QueueTest {

    @Test
    void 대기_순번이_0이고_활성화_토큰이_200개_미만일_때_ACTIVE_상태의_토큰_생성() {
        // given
        Long userId = 1L;
        Long activeCount = 30L;
        Long rank = 0L;

        // when
        Queue token = Queue.createToken(userId, activeCount, rank);

        // then
        assertThat(token.status()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(token.rank()).isEqualTo(0L);
        assertThat(token.expiredAt()).isNotNull();
    }

    @Test
    void 대기_순번이_1_이상일_때_WAITING_상태의_토큰_생성() {
        // given
        Long userId = 1L;
        Long activeCount = 50L;
        Long rank = 1L;

        // when
        Queue token = Queue.createToken(userId, activeCount, rank);

        // then
        assertThat(token.status()).isEqualTo(QueueStatus.WAITING);
        assertThat(token.rank()).isEqualTo(2L); // rank 는 activeCount 가 50 이상일 때에는 대기해야 하므로 +1 추가됨.
        assertThat(token.expiredAt()).isNull();
    }

    @Test
    void 토큰을_만료_상태로_변경한다() {
        // given
        Queue token = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        // when
        Queue expiredToken = token.expired();

        // then
        assertThat(expiredToken.status()).isEqualTo(QueueStatus.EXPIRED);
        assertThat(expiredToken.expiredAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 토큰_상태_체크_시_ACTIVE라면_성공() {
        // given
        Queue token = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .build();

        // when
        boolean isActive = token.checkStatus();

        // then
        assertThat(isActive).isTrue();
    }

    @Test
    void 토큰_유효성_검증_시_활성_상태라면_성공() {
        // given
        Queue validToken = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        // when & then
        validToken.validateToken(); // 예외가 발생하지 않으면 성공
    }
}
