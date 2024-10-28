package hhplus.concert.infra.repository.jpa;

import hhplus.concert.infra.entity.ReservationEntity;
import hhplus.concert.support.type.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from reservation r where r.id = ?1")
    Optional<ReservationEntity> findByReservationId(Long reservationId);
    List<ReservationEntity> findByStatusAndReservationAtBefore(ReservationStatus reservationStatus, LocalDateTime localDateTime);

    List<ReservationEntity> findByConcertIdAndScheduleIdAndSeatId(Long concertId, Long scheduleId, Long seatId);
}
