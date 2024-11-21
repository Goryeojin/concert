package hhplus.concert.interfaces.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import hhplus.concert.interfaces.kafka.payload.PaymentMessagePayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class ConcertPaymentMessageConsumer implements KafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private String receivedMessage;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        log.info("Received message: {}", message);
        this.receivedMessage = message;
    }

    @Override
    @KafkaListener(topics = "concert-payment", groupId = "concert")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        log.info("Received headers: {}, payload: {}", message.getHeaders(), message.getPayload());
        try {
            PaymentMessagePayload paymentMessagePayload = objectMapper.readValue(message.getPayload(), PaymentMessagePayload.class);

            String paymentType = new String(message.getHeaders().get("payment-type", byte[].class), StandardCharsets.UTF_8);

            if ("COMPLETED".equals(paymentType)) {
                // 결제 내역 타 플랫폼 전달 등 로직 수행
            } else if ("CANCELLED".equals(paymentType)) {
                // 결제 취소 유스케이스가 생성되면 사용할 수 있음
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing Kafka message", e);
        }
    }
}
