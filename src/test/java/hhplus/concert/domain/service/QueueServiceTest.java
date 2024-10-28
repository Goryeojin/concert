package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.type.QueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 토큰을_생성한다() {
        // given
        Long userId = 1L;
        when(queueRepository.findByStatus(QueueStatus.ACTIVE)).thenReturn(10L);
        when(queueRepository.findByStatus(QueueStatus.WAITING)).thenReturn(5L);

        Queue expectedToken = Queue.createToken(userId, 10L, 5L);
        given(queueRepository.save(any(Queue.class)))
                .willReturn(expectedToken);

        // when
        Queue token = queueService.createToken(userId);

        // then
        assertThat(token)
                .usingRecursiveComparison()
                .ignoringFields("token", "createdAt")
                .isEqualTo(expectedToken);
        verify(queueRepository, times(1)).save(any(Queue.class));
    }

    @Test
    void 토큰의_상태를_만료로_변경시킨다() {
        // given
        Queue token = mock(Queue.class);
        Queue expiredToken = mock(Queue.class);
        when(token.expired()).thenReturn(expiredToken);

        // when
        queueService.expireToken(token);

        // then
        verify(queueRepository, times(1)).expireToken(expiredToken);
    }

    @Test
    void 토큰_상태가_EXPIRED라면_대기열_체크시_에러를_반환한다() {
        // given
        Queue expiredQueue = Queue.builder()
                .status(QueueStatus.EXPIRED)
                .build();

        // when & then
        CoreException exception = assertThrows(CoreException.class,
                () -> queueService.checkQueueStatus(expiredQueue));

        // 예외코드 UNAUTHORIZED
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    void 토큰_상태가_ACTIVE라면_대기열_체크시_바로_반환한다() {
        // given
        Queue queue = mock(Queue.class);
        when(queue.checkStatus()).thenReturn(true); // 활성화 상태

        // when
        Queue result = queueService.checkQueueStatus(queue);

        // then
        assertThat(result).isEqualTo(queue);
    }

    @Test
    void 토큰_상태가_WAITING이라면_대기열_체크시_대기순서를_조회하고_반환한다() {
        // given
        Queue queue = mock(Queue.class);
        when(queue.checkStatus()).thenReturn(false); // WAITING
        when(queueRepository.findUserRank(queue.id())).thenReturn(3L);

        // when
        Queue result = queueService.checkQueueStatus(queue);

        // then
        assertThat(result.rank()).isEqualTo(3L);
    }

    @Test
    void 토큰_유효성_체크_대기상태거나_만료한다면_에러를_반환한다() {
        // given
        String tokenString = "token";
        Queue queue = mock(Queue.class);
        when(queueRepository.findQueue(tokenString)).thenReturn(queue);
        doThrow(new CoreException(ErrorCode.UNAUTHORIZED)).when(queue).validateToken();

        // when & then
        assertThrows(CoreException.class, () -> queueService.validateToken(tokenString));
    }

    @Test
    void 토큰_유효성_체크_활성상태라면_성공한다() {
        // given
        String tokenString = "token";
        Queue queue = mock(Queue.class);
        when(queueRepository.findQueue(tokenString)).thenReturn(queue);

        // when
        Queue result = queueService.validateToken(tokenString);

        // then
        assertThat(result).isEqualTo(queue);
    }

}