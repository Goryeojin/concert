package hhplus.concert.kafka;

import hhplus.concert.infra.kafka.producer.KafkaMessageProducer;
import hhplus.concert.interfaces.kafka.consumer.ConcertPaymentMessageConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KafkaIntegrationTest {

    @Autowired
    ConcertPaymentMessageConsumer consumer;

    @Autowired
    KafkaMessageProducer producer;

    @Test
    void 카프카로_메시지를_발행하면_정상적으로_소비한다() throws InterruptedException {
        // given
        String message = "test_message";

        // when
        producer.send(message);

        // then
        Thread.sleep(1000);
        assertThat(consumer.getReceivedMessage()).isEqualTo(message);
    }
}