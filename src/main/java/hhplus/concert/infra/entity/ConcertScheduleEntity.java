package hhplus.concert.infra.entity;

import hhplus.concert.domain.model.ConcertSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "concert_schedule")
@Table(indexes = {
        @Index(name = "idx_concert_id", columnList = "concert_id"),
        @Index(name = "idx_concert_schedule_date", columnList = "concert_id, reservation_at, deadline")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertEntity concert;

    @Column(nullable = false)
    private LocalDateTime reservationAt;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private LocalDateTime concertAt;

    public ConcertSchedule of() {
        return ConcertSchedule.builder()
                .id(id)
                .concertId(concert.getId())
                .reservationAt(reservationAt)
                .deadline(deadline)
                .concertAt(concertAt)
                .build();
    }

    public static ConcertScheduleEntity from(ConcertSchedule schedule) {
        return ConcertScheduleEntity.builder()
                .concert(ConcertEntity.builder().id(schedule.concertId()).build())
                .reservationAt(schedule.reservationAt())
                .deadline(schedule.deadline())
                .concertAt(schedule.concertAt())
                .build();
    }
}
