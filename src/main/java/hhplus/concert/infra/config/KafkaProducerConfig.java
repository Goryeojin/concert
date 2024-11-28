package hhplus.concert.infra.config;

import hhplus.concert.interfaces.interceptor.KafkaProducerInterceptor;
import hhplus.concert.interfaces.kafka.listener.KafkaProducerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(DefaultKafkaProducerFactory<String, String> factory, KafkaProducerInterceptor interceptor, KafkaProducerListener listener) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(factory);
        kafkaTemplate.setProducerInterceptor(interceptor);
        kafkaTemplate.setProducerListener(listener);
        return kafkaTemplate;
    }
}
