package hhplus.concert.infra.entity;

import hhplus.concert.domain.model.Reservation;
import hhplus.concert.support.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "reservation")
@Table(indexes = {
        @Index(name = "idx_reservation_user_id", columnList = "user_id"),
        @Index(name = "idx_reservation_status", columnList = "status"),
        @Index(name = "idx_concert_schedule_seat_id", columnList = "concert_id, concert_schedule_id, seat_id")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private ConcertEntity concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservationAt;

    public static ReservationEntity from(Reservation reservation) {
        return ReservationEntity.builder()
                .id(reservation.id())
                .concert(ConcertEntity.builder().id(reservation.concertId()).build())
                .schedule(ConcertScheduleEntity.builder().id(reservation.scheduleId()).build())
                .seat(SeatEntity.builder().id(reservation.seatId()).build())
                .user(UserEntity.builder().id(reservation.userId()).build())
                .status(reservation.status())
                .reservationAt(reservation.reservationAt())
                .build();
    }

    public Reservation of() {
        return Reservation.builder()
                .id(id)
                .concertId(concert.getId())
                .scheduleId(schedule.getId())
                .seatId(seat.getId())
                .userId(user.getId())
                .status(status)
                .reservationAt(reservationAt)
                .build();
    }
}
