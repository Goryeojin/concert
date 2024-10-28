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

    public Reservation of(ReservationEntity entity) {
        return Reservation.builder()
                .id(entity.id)
                .concertId(entity.getConcert().getId())
                .scheduleId(entity.getSchedule().getId())
                .seatId(entity.getSeat().getId())
                .userId(entity.getUser().getId())
                .status(entity.status)
                .reservationAt(entity.getReservationAt())
                .build();
    }
}
