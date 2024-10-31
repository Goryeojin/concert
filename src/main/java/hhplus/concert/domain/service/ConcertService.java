package hhplus.concert.domain.service;

import hhplus.concert.domain.model.Concert;
import hhplus.concert.domain.model.ConcertSchedule;
import hhplus.concert.domain.model.Seat;
import hhplus.concert.domain.repository.ConcertRepository;
import hhplus.concert.support.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public List<Concert> getConcerts() {
        return concertRepository.findConcerts();
    }

    public Concert getConcert(Long concertId) {
        return concertRepository.findConcert(concertId);
    }

    public List<ConcertSchedule> getConcertSchedules(Concert concert) {
        // 해당 콘서트가 예약 가능한지 확인
        if (!concert.checkStatus()) {
            return null;
        }
        return concertRepository.findConcertSchedules(concert.id());
    }

    public List<Seat> getSeats(Long concertId, Long scheduleId, SeatStatus status) {
        return concertRepository.findSeats(concertId, scheduleId, status);
    }

    public ConcertSchedule scheduleInfo(Long scheduleId) {
        return concertRepository.findConcertSchedule(scheduleId);
    }

    public void isAvailableReservation(ConcertSchedule schedule, Seat seat) {
        // 예약 가능 상태인지 확인
        schedule.checkStatus();
        // 예약 가능 좌석인지 확인
        seat.checkStatus();
    }

    @Transactional
    public Seat getSeat(Long seatId) {
        return concertRepository.findSeat(seatId);
    }

    public void assignmentSeat(Seat seat) {
        Seat assignment = seat.assign();
        concertRepository.saveSeat(assignment);
    }

    public Seat getSeatWithoutLock(Long seatId) {
        return concertRepository.findById(seatId);
    }
}
