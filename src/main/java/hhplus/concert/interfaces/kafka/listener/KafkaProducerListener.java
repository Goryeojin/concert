package hhplus.concert.interfaces.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hhplus.concert.domain.dto.PaymentEventCommand;
import hhplus.concert.domain.model.PaymentEvent;
import hhplus.concert.support.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerListener implements ProducerListener<String, String> {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
        ProducerListener.super.onSuccess(producerRecord, recordMetadata);
        try {
            PaymentEventCommand command = objectMapper.readValue(producerRecord.value(), PaymentEventCommand.class);
            eventPublisher.publishEvent(PaymentEvent.from(producerRecord.topic(), OutboxStatus.SEND_SUCCESS, command));

            log.info("kafka producer on success topic: {}, key: {}, payload: {}", producerRecord.topic(), producerRecord.key(), command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        ProducerListener.super.onError(producerRecord, recordMetadata, exception);
        try {
            PaymentEventCommand command = objectMapper.readValue(producerRecord.value(), PaymentEventCommand.class);
            eventPublisher.publishEvent(PaymentEvent.from(producerRecord.topic(), OutboxStatus.SEND_FAIL, command));

            log.info("kafka producer on error topic: {}, key: {}, payload: {}", producerRecord.topic(), producerRecord.key(), command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
