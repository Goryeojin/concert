package hhplus.concert.application.facade;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class QueueFacadeIntegrationTest {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private QueueRepository queueRepository;

    private final Long USER_ID = 1L;

    @Test
    @Transactional
    void 토큰을_생성한다() {
        // given
        Long userId = USER_ID;

        // when
        Queue token = queueFacade.createToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token.userId()).isEqualTo(userId);
        assertThat(token.status()).isEqualTo(QueueStatus.ACTIVE);
    }

    @Test
    void 토큰_생성_시_기존_토큰은_만료_상태로_변경한다() {
        // given
        Long userId = USER_ID;

        // when
        Queue oldToken = queueFacade.createToken(userId);
        queueFacade.createToken(userId); // 같은 사용자로 토큰을 새로 발급 요청한다.

        Queue queue = queueRepository.findQueue(oldToken.token());

        // then
        assertThat(queue).isNotNull();
        assertThat(queue.userId()).isEqualTo(userId);
        assertThat(queue.status()).isEqualTo(QueueStatus.EXPIRED);
    }

    @Test
    @Transactional
    void 만료된_토큰_상태_조회시_예외_발생() {
        // given
        Long userId = USER_ID;
        String uuid = "1234";
        Queue token = Queue.builder()
                .id(USER_ID)
                .token(uuid)
                .userId(userId)
                .status(QueueStatus.EXPIRED)
                .createdAt(LocalDateTime.now())
                .build();

        queueRepository.save(token);

        // when & then
        assertThatThrownBy(() -> queueFacade.getStatus(token.token(), userId))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    void 대기열_상태_조회_성공() {
        // given
        Long userId = USER_ID;
        Queue token = queueFacade.createToken(userId);

        // when
        Queue queueStatus = queueFacade.getStatus(token.token(), userId);

        // then
        assertThat(queueStatus).isNotNull();
        assertThat(queueStatus.token()).isEqualTo(token.token());
        assertThat(queueStatus.status()).isEqualTo(QueueStatus.ACTIVE);
    }

    @Test
    @Transactional
    void 토큰이_활성_상태인_사용자가_50명인_경우_이후_요청자는_대기_상태의_토큰을_받는다() {
        // given
        List<Queue> tokenList = new ArrayList<>();
        int activeCnt = 0;
        int waitingCnt = 0;

        // 50명의 대기자를 대기열에 추가
        for(long l = 1; l <= 100; l++) {
            Queue dummyToken = queueFacade.createToken(l);
            tokenList.add(dummyToken);
        }

        // when
        Queue token = queueFacade.createToken(51L);

        // then
        // 앞선 50명의 토큰이 ACTIVE 상태인지 검증
        for (Queue queue : tokenList) {
            QueueStatus status = queue.status();
            if (status.equals(QueueStatus.ACTIVE)) {
                activeCnt++;
            } else if (status.equals(QueueStatus.WAITING)){
                waitingCnt++;
            }
        }

        // 충전 요청 성공 횟수가 스레드 갯수와 같은지 검증한다.
        assertThat(activeCnt + waitingCnt).isEqualTo(100);

        // 충전된 금액의 정합성이 보장되는지 검증한다.
        assertThat(activeCnt).isEqualTo(50);
    }
}
