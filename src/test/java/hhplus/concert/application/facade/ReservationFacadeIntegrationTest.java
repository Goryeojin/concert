package hhplus.concert.application.facade;

import hhplus.concert.application.dto.ReservationCommand;
import hhplus.concert.application.dto.ReservationResult;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Reservation;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.repository.ConcertRepository;
import hhplus.concert.domain.repository.ReservationRepository;
import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.type.ReservationStatus;
import hhplus.concert.support.type.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class ReservationFacadeIntegrationTest {
    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private final Long USER_ID = 1L;

    @Test
    @Transactional
    void 예약_가능_시간_이전에_예약_요청_시_BEFORE_RESERVATION_AT_에러를_반환한다() {
        // given
        ConcertSchedule beforeReservationAtSchedule =
                concertRepository.findConcertSchedule(3L);
        ReservationCommand command = new ReservationCommand(USER_ID, 1L, beforeReservationAtSchedule.id(), 1L);

        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(command))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BEFORE_RESERVATION_AT);

    }
    
    @Test
    @Transactional
    void 예약_마감_시간_이후에_예약_요청_시_AFTER_DEADLINE_에러를_반환한다() {
        // given
        ConcertSchedule afterDeadlineSchedule =
                concertRepository.findConcertSchedule(4L);
        ReservationCommand command = new ReservationCommand(USER_ID, 1L, afterDeadlineSchedule.id(), 1L);

        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(command))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AFTER_DEADLINE);
    }
    
    @Test
    @Transactional
    void 좌석의_상태가_UNAVAILABLE_이라면_SEAT_UNAVAILABLE_에러를_반환한다() {
        // given
        ConcertSchedule schedule = concertRepository.findConcertSchedule(1L); // 예약 가능한 콘서트 일정
        Seat seat = concertRepository.findSeat(26L); // 상태가 UNAVAILABLE 인 좌석
        ReservationCommand command = new ReservationCommand(USER_ID, 1L, schedule.id(), seat.id());

        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(command))
                .isInstanceOf(CoreException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_UNAVAILABLE);
    }
    
    @Test
    @Transactional
    void 예약_가능한_시간이고_좌석이_예약_가능_상태라면_예약_정보를_생성하고_반환한다() {
        // given
        ConcertSchedule schedule = concertRepository.findConcertSchedule(1L); // 예약 가능한 콘서트 일정
        Seat seat = concertRepository.findSeat(1L); // 상태가 AVAILABLE 인 좌석
        ReservationCommand command = new ReservationCommand(USER_ID, 1L, schedule.id(), seat.id());

        // when
        ReservationResult reservation = reservationFacade.reservation(command);

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.PAYMENT_WAITING); // 결제 대기 상태로 변경되었는지 검증
    }

    @Test
    @Transactional
    void 다수의_사용자가_1개의_좌석을_동시에_예약하면_한_명만_성공한다() throws InterruptedException {
        // when
        final int threadCount = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (long l = 1; l <= threadCount; l++) {
            // 좌석 예약 요청 객체 생성
            ReservationCommand command = ReservationCommand.builder()
                    .userId(l)
                    .concertId(1L)
                    .scheduleId(1L)
                    .seatId(1L)
                    .build();

            executorService.submit(() -> {
                try {
                    // 좌석 예약 호출
                    reservationFacade.reservation(command);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        List<Reservation> reservations = reservationRepository.findByConcertIdAndScheduleIdAndSeatId(1L, 1L, 1L);
        // 같은 콘서트 같은 일정 같은 좌석으로 예약이 하나만 잡혔는지 검증한다.
        assertThat(reservations.size()).isOne();
        // 해당 좌석이 UNAVAILABLE 상태로 변경되었는지 검증한다.
        assertThat(concertRepository.findSeat(1L).status()).isEqualTo(SeatStatus.UNAVAILABLE);
    }
}