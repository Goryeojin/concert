package hhplus.concert.domain.component.port;

import hhplus.concert.domain.model.PaymentEvent;

public interface MessageProducer {

    void send(PaymentEvent event);
}
