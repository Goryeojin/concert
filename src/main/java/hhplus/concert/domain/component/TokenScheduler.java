package hhplus.concert.domain.component;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.domain.repository.QueueRepository;
import hhplus.concert.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final QueueRepository queueRepository;

    // 만료된 토큰 상태 변경
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void expireTokens() {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        // 만료 시간이 현재 시간보다 이전이고 ACTIVE 인 토큰 조회
        List<Queue> expiredTokens = queueRepository.findExpiredTokens(now, QueueStatus.ACTIVE);
        // 만료된 토큰 상태를 EXPIRED 로 변경
        for (Queue token : expiredTokens) {
            Queue expired = token.expired();
            queueRepository.save(expired); // 변경된 상태 저장
        }
    }

    // ACTIVE 토큰 수 조정
    public void manageActiveTokens() {
        // ACTIVE 상태의 토큰 수 조회
        long activeCount = queueRepository.findByStatus(QueueStatus.ACTIVE);
        // ACTIVE 토큰이 50개 미만인 경우
        if (activeCount < 50) {
            // 대기 중인 토큰 조회
            long neededTokens = 50 - activeCount;
            List<Queue> waitingTokens = queueRepository.findWaitingTokens(neededTokens); // 필요 수만큼 대기 중인 토큰 조회
            for (Queue token : waitingTokens) {
                Queue activate = token.activate();// 토큰을 ACTIVE 상태로 변경
                queueRepository.save(activate); // 변경된 토큰 저장
            }
        }
    }
}
