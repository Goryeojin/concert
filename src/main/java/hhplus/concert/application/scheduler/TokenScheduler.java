package hhplus.concert.application.scheduler;

import hhplus.concert.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final QueueService queueService;

    // ACTIVE 토큰 수 조정
    @Scheduled(fixedDelay = 10000)
    public void adjustActiveToken() {
        queueService.updateActiveTokens();
    }
}
