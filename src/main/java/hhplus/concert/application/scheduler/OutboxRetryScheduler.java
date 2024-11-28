package hhplus.concert.application.scheduler;

import hhplus.concert.domain.component.port.MessageProducer;
import hhplus.concert.domain.model.OutboxEvent;
import hhplus.concert.domain.model.PaymentEvent;
import hhplus.concert.domain.repository.OutboxRepository;
import hhplus.concert.support.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRetryScheduler {

    private final OutboxRepository outboxRepository;
    private final MessageProducer producer;

    @Scheduled(fixedDelay = 1000)
    public void retryFailedOutboxEvents() {
        List<OutboxEvent> failedEvents = outboxRepository.findByStatusNot(OutboxStatus.SEND_SUCCESS.name());

        failedEvents.forEach(event -> {
            // 발행한지 1분이 채 지나지 않았다면 재발행하지 않음
            if (event.getStatus().equals(OutboxStatus.INIT.name()) && event.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) return;
            producer.send(PaymentEvent.mapToPaymentEvent(event));
        });
    }
}
