package hhplus.concert.infra.entity;

import hhplus.concert.domain.model.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "payment")
@Table(indexes = @Index(name = "idx_payment_user_id", columnList = "user_id"))
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private int amount;

    private LocalDateTime paymentAt;

    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.id())
                .reservation(ReservationEntity.builder().id(payment.reservationId()).build())
                .user(UserEntity.builder().id(payment.userId()).build())
                .amount(payment.amount())
                .paymentAt(payment.paymentAt())
                .build();
    }

    public Payment of() {
        return Payment.builder()
                .id(id)
                .reservationId(reservation.getId())
                .userId(user.getId())
                .amount(amount)
                .paymentAt(paymentAt)
                .build();
    }
}
