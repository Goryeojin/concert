package hhplus.concert.domain.component;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.type.QueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TokenSchedulerTest {

    @InjectMocks
    private TokenScheduler tokenScheduler;

    @Mock
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 만료시간이_지난_토큰들의_상태를_EXPIRED로_변경한다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Queue token1 = Queue.builder().userId(1L).token("1").status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(10)).enteredAt(now.minusMinutes(10)).expiredAt(now.minusMinutes(10))
                .build();
        Queue token2 = Queue.builder().userId(2L).token("2").status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(10)).enteredAt(now.minusMinutes(10)).expiredAt(now.minusMinutes(10))
                .build();

        List<Queue> tokens = List.of(token1, token2);

        given(queueRepository.findExpiredTokens(any(LocalDateTime.class), eq(QueueStatus.ACTIVE)))
                .willReturn(tokens);

        // when
        tokenScheduler.expireTokens();

        // then
        verify(queueRepository, times(1)).findExpiredTokens(any(LocalDateTime.class), eq(QueueStatus.ACTIVE));
        verify(queueRepository, times(1)).save(argThat(savedToken ->
                savedToken.status() == QueueStatus.EXPIRED &&
                        savedToken.userId().equals(token1.userId()) &&
                        savedToken.token().equals(token1.token())
        ));

        verify(queueRepository, times(1)).save(argThat(savedToken ->
                savedToken.status() == QueueStatus.EXPIRED &&
                        savedToken.userId().equals(token2.userId()) &&
                        savedToken.token().equals(token2.token())
        ));
    }

    @Test
    void ACTIVE_토큰_수_부족한_수만큼만_조회_후_상태_변경() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Queue token1 = Queue.builder().userId(1L).token("1").status(QueueStatus.WAITING)
                .createdAt(now.minusMinutes(10))
                .build();
        Queue token2 = Queue.builder().userId(2L).token("2").status(QueueStatus.WAITING)
                .createdAt(now.minusMinutes(10))
                .build();
        List<Queue> tokens = List.of(token1, token2);

        when(queueRepository.findByStatus(QueueStatus.ACTIVE)).thenReturn(48L);
        given(queueRepository.findWaitingTokens(2L))
                .willReturn(tokens);

        // when
        tokenScheduler.manageActiveTokens();

        // then
        verify(queueRepository, times(1)).findByStatus(QueueStatus.ACTIVE);
        verify(queueRepository, times(1)).findWaitingTokens(2L); // 필요한 수만큼 조회
        verify(queueRepository, times(1)).save(argThat(savedToken ->
                savedToken.status() == QueueStatus.ACTIVE &&
                        savedToken.userId().equals(token1.userId()) &&
                        savedToken.token().equals(token1.token())
        ));

        verify(queueRepository, times(1)).save(argThat(savedToken ->
                savedToken.status() == QueueStatus.ACTIVE &&
                        savedToken.userId().equals(token2.userId()) &&
                        savedToken.token().equals(token2.token())
        ));
    }

}