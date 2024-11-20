package hhplus.concert.application.component.event;

import hhplus.concert.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void success(Payment bill) {
        try {
            // 타 플랫폼으로 결제 내역 전송
            applicationEventPublisher.publishEvent(bill);
        } catch (Exception e) {
            // 예외를 처리하고 롤백되지 않도록 한다
            log.error("Failed to publish payment event: ", e);
        }
    }
}
