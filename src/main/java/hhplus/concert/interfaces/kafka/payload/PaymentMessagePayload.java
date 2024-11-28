package hhplus.concert.interfaces.kafka.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentMessagePayload {
    private Long id;
    private Long reservationId;
    private Long userId;
    private int amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime paymentAt;
}
