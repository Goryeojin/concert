package hhplus.concert.infra.entity;

import hhplus.concert.domain.model.Seat;
import hhplus.concert.support.type.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "seat")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id", nullable = false)
    private ConcertScheduleEntity concertSchedule;

    @Column(nullable = false)
    private int seatNo;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime reservationAt;

    @Column(nullable = false)
    private int seatPrice;

    @Version
    private Long version;

    public Seat of() {
        return Seat.builder()
                .id(this.id)
                .concertScheduleId(this.concertSchedule.getId())
                .seatNo(this.seatNo)
                .status(this.status)
                .reservationAt(this.reservationAt)
                .seatPrice(this.seatPrice)
                .version(this.version)
                .build();
    }

    public static SeatEntity from(Seat seat) {
        return SeatEntity.builder()
                .id(seat.id())
                .concertSchedule(ConcertScheduleEntity.builder().id(seat.concertScheduleId()).build())
                .seatNo(seat.seatNo())
                .status(seat.status())
                .reservationAt(seat.reservationAt())
                .seatPrice(seat.seatPrice())
                .version(seat.version())
                .build();
    }
}
