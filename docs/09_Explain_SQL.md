# API 쿼리 분석 및 인덱스 개선

## 쿼리 조회가 필요한 API
1. 콘서트 조회: `GET` `/api/v1/concerts`
2. 예약 가능 일정 조회: `GET` `/api/v1/concerts/{concertId}/schedules`
3. 예약 가능 좌석 조회: `GET` `/api/v1/concerts/{concertId}/schedules/{scheduleId}/seats`
4. 포인트 조회: `GET` `/api/v1/users/{userId}/point`
5. 포인트 충전: `PATCH` `/api/v1/users/{userId}/point`
6. 콘서트 좌석 예약: `POST` `/api/v1/reservations`
7. 결제: `POST` `/api/v1/payments`

### 쿼리 분석
#### 1. 콘서트 조회
- 설명: 사용자가 콘서트 목록을 조회하기 위해 사용된다.
- 빈도: ★★★★★
  - 콘서트 목록을 조회할 때마다 호출되므로 자주 사용된다.
- 복잡도: ★☆☆☆☆
  - 하나의 테이블의 모든 컬럼을 조회한다.
- 쿼리
```sql
select 
  ce1_0.id,
  ce1_0.description,
  ce1_0.status,
  ce1_0.title 
from 
  concert ce1_0
```

#### 2. 예약 가능 일정 조회
- 설명: 사용자가 특정 콘서트의 예약 가능한 일정을 조회하기 위해 사용된다. `concert_schedule` 테이블과 `concert` 테이블에서 `concert_id와` `reservation_at`, `deadline` 조건에 맞는 데이터를 조회한다.
- 빈도: ★★★★★
  - 예약 가능 일정을 조회할 때마다 호출되므로 자주 사용된다.
- 복잡도: ★★★☆☆
  - 두 개의 테이블을 조회하며, ID와 날짜 범위를 사용하여 필터링한다.
  - `concert_id`를 기준으로 `concert` 테이블과 `concert_schedule` 테이블을 조인한다.
- 쿼리
  1. `concert` 테이블에서 `concert_id`로 특정 콘서트를 조회하는 쿼리
  2. `concert_schedule` 테이블에서 `concert_id와` `reservation_at`, `deadline` 범위로 콘서트 일정을 조회하는 쿼리
```sql
select 
  ce1_0.id,
  ce1_0.description,
  ce1_0.status,
  ce1_0.title 
from 
  concert ce1_0 
where 
  ce1_0.id=?
  
select 
  cse1_0.id,
  cse1_0.concert_id,
  cse1_0.concert_at,
  cse1_0.deadline,
  cse1_0.reservation_at 
from 
  concert_schedule cse1_0 
where 
  cse1_0.concert_id=? 
and 
  cse1_0.reservation_at<? 
and 
  cse1_0.deadline>?
```

#### 3. 예약 가능 좌석 조회
- 설명: 사용자가 특정 콘서트의 예약 가능한 좌석을 조회하기 위해 사용된다. `seat` 테이블과 `concert_schedule` 테이블에서 `concert_schedule_id` 를 조인, `concert` 테이블에서 `concert_id` 를 조인, `status` 조건에 맞는 데이터를 조회한다.
- 빈도: ★★★★★
  - 예약 가능한 좌석을 조회할 때마다 호출되므로 자주 사용된다.
- 복잡도: ★★★☆☆
  - 두 개의 테이블과 조인하고, 좌석의 상태를 필터링해야 하므로 복잡하다.
  - `concert_id` 를 기준으로 `concert` 테이블과 조인하고, `concert_schedule_id` 를 기준으로 `concert_schedule` 테이블과 조인하고, `status` 필드를 검사한다.
- 쿼리
  1. `concert` 테이블에서 `concert_id` 로 특정 콘서트를 조회하는 쿼리
  2. `concert_schedule` 테이블에서 `concert_schedule_id` 로 특정 콘서트 일정을 조회하는 쿼리
  3. `seat` 테이블에서 `status` 로 예약 가능한 좌석을 조회하는 쿼리
```sql
select 
  ce1_0.id,
  ce1_0.description,
  ce1_0.status,
  ce1_0.title 
from 
  concert ce1_0 
where 
  ce1_0.id=?
  
select 
  cse1_0.id,
  cse1_0.concert_id,
  cse1_0.concert_at,
  cse1_0.deadline,
  cse1_0.reservation_at 
from 
  concert_schedule cse1_0 
where 
  cse1_0.id=?
  
select 
  se1_0.id,
  se1_0.concert_schedule_id,
  se1_0.reservation_at,
  se1_0.seat_no,
  se1_0.seat_price,
  se1_0.status 
from 
  seat se1_0 
join 
  concert_schedule cs1_0 on cs1_0.id=se1_0.concert_schedule_id 
where 
  se1_0.concert_schedule_id=? 
and 
  cs1_0.concert_id=? 
and 
  se1_0.status=?
```

#### 4. 포인트 조회
- 설명: 특정 사용자의 포인트 잔액을 조회한다. `point` 테이블에서 사용자의 잔액 관련 데이터를 가져온다.
- 빈도: ★★★☆☆
  - 포인트 잔액을 확인할 때마다 조회되지만, 다른 서비스에 비해 상대적으로 빈도가 낮을 수 있다.
- 복잡도: ★☆☆☆☆
  - 단일 테이블에서 하나의 조건으로 데이터를 조회하기 때문에 복잡도가 낮다.
- 쿼리
  1. `user_id` 로 포인트 정보를 조회하는 쿼리
```sql
select 
  pe1_0.id,
  pe1_0.amount,
  pe1_0.last_updated_at,
  pe1_0.user_id 
from 
  point pe1_0 
where 
  pe1_0.user_id=?
```

#### 5. 포인트 충전
- 설명: 특정 사용자의 포인트를 충전(업데이트)하는 데에 사용된다.
- 빈도: ★★★☆☆
  - 잔액 충전은 결제 시 자주 이용된다.
- 복잡도: ★☆☆☆☆
  - 단일 테이블에서 하나의 조건으로 데이터를 업데이트하기 때문에 복잡도가 낮다.
- 쿼리
  1. `point` 테이블의 `id` 로 사용자의 잔액을 업데이트하는 쿼리
```sql
update 
  point 
set 
  amount=?,
  last_updated_at=?,
  user_id=? 
where 
  id=?
```

#### 6. 콘서트 좌석 예약
- 설명: 특정 콘서트 일정에 대한 좌석 정보를 조회하고, 좌석 예약을 처리한다. concert_schedule 과 seat 테이블에서 id 로 조회하고, 필요한 경우 좌석 상태를 변경하거나 예약 테이블에 데이터를 삽입한다.
- 빈도: ★★★★☆
  - 예약 요청은 사용자가 특정 콘서트의 좌석을 예약할 때마다 발생하므로 자주 사용된다.
  - 특정 좌석에 대한 예약은 거의 1:1로 대응되는 만큼 발생 빈도수는 한정적이다.
- 복잡도: ★★★★☆
  - 여러 테이블을 조회하고, 데이터를 삽입하거나 업데이트한다. 좌석의 예약 가능 여부를 확인하고, 예약을 처리하는 과정에서 데이터 무결성을 유지해야 한다.
- 쿼리
  1. `concert_schedule` 테이블에서 `schedule_id` 로 조회하는 쿼리
  2. `seat` 테이블에서 `id` 로 조회하는 쿼리
  3. `reservation` 테이블에 새로운 예약 데이터를 삽입하는 쿼리
  4. `seat` 테이블에서 `id` 로 특정 좌석의 상태를 업데이트하는 쿼리
```sql
select 
  cse1_0.id,
  cse1_0.concert_id,
  cse1_0.concert_at,
  cse1_0.deadline,
  cse1_0.reservation_at 
from 
  concert_schedule cse1_0 
where 
  cse1_0.id=?
  
select 
  se1_0.id,
  se1_0.concert_schedule_id,
  se1_0.reservation_at,
  se1_0.seat_no,
  se1_0.seat_price,
  se1_0.status 
from 
  seat se1_0 
where 
  se1_0.id=?
  
insert into reservation (concert_id,reservation_at,concert_schedule_id,seat_id,status,user_id,id) 
values (?,?,?,?,?,?,default)

update 
  seat 
set 
  concert_schedule_id=?,
  reservation_at=?,
  seat_no=?,
  seat_price=?,
  status=? 
where 
  id=?
```

#### 7. 결제
- 설명: 결제 처리를 위한 데이터 조회를 위해 다양한 테이블을 조회한다. `reservation`, `point`, `seat` 테이블을 조회하여 데이터의 일관성과 무결성을 유지한다.
- 빈도: ★★★★★
  - 결제는 콘서트 예약에서 자주 발생한다. 
- 복잡도: ★★★☆☆
  - 단일 테이블에서 간단한 조건으로 조회되지만, 데이터의 일관성 유지와 트랜잭션 처리가 있어 복잡도를 가진다.
- 쿼리
  1. `reservation` 테이블에서 `id` 로 예약 데이터를 조회하는 쿼리
  2. `seat` 테이블에서 `id` 로 좌석 데이터를 조회하는 쿼리
  3. `point` 테이블에서 `user_id` 로 잔액을 조회하는 쿼리
  4. `payment` 테이블에 결제 내역 데이터를 삽입하는 쿼리
  5. `reservation` 테이블의 예약 상태를 업데이트하는 쿼리
  6. `point` 테이블의 잔액을 업데이트하는 쿼리
```sql
select 
  re1_0.id,
  re1_0.concert_id,
  re1_0.reservation_at,
  re1_0.concert_schedule_id,
  re1_0.seat_id,
  re1_0.status,
  re1_0.user_id 
from 
  reservation re1_0 
where 
  re1_0.id=?
  
select 
  se1_0.id,
  se1_0.concert_schedule_id,
  se1_0.reservation_at,
  se1_0.seat_no,
  se1_0.seat_price,
  se1_0.status 
from 
  seat se1_0 
where 
  se1_0.id=?

select 
  pe1_0.id,
  pe1_0.amount,
  pe1_0.last_updated_at,
  pe1_0.user_id 
from 
  point pe1_0 
where 
  pe1_0.user_id=?

insert into payment (amount, payment_at, reservation_id, user_id,id) 
values (?, ?, ?, ?, default)

update 
  reservation 
set 
  concert_id=?,
  reservation_at=?,
  concert_schedule_id=?,
  seat_id=?,
  status=?,
  user_id=? 
where 
  id=?

update 
  point 
set 
  amount=?,
  last_updated_at=?,
  user_id=? 
where 
  id=?
```

## 인덱스 적절성 판단 및 설정
### concert
- 현재 API 는 검색 조건이 존재하지 않기 때문에 인덱스는 불필요하다.
- `concert` 의 특성을 고려할 때, 향후 검색 요구가 발생할 가능성을 대비해볼 수 있다.
- `concert` 는 생성 빈도가 낮아 인덱스 생성이 큰 부담이 되지 않는다.

1. `title` 컬럼 인덱스
- 필요성: concert 제목으로 검색하는 경우가 발생할 수 있다. 중복값이 적고, 검색 성능 향상에 기여할 수 있다.
- cardinality: 높음
- 변경 빈도: 낮음

2. `status` 컬럼 인덱스
- 필요성: 예약 가능 상태를 기준으로 조회하는 경우가 발생할 수 있다.
- cardinality: 낮음 (열거형으로 여러 콘서트가 동일한 상태를 가질 수 있음)
- 변경 빈도: 중간 (예약 이벤트가 종료되면 상태 변경 가능)

#### 인덱스 설정
```java
@Table(indexes = {
        @Index(name = "idx_concert_title", columnList = "title"),
        @Index(name = "idx_concert_status", columnList = "status")
})
public class ConcertEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    private String description;
    
    @Enumerated(value = EnumType.STRING)
    private ConcertStatus status;
}
```

### concert_schedule
#### 단일 인덱스
1. `concert_id`
- 필요성: 대부분 쿼리에서 `concert_id` 를 사용하여 `concert_schedule` 테이블을 조회한다. 
- cardinality: 중간 (여러 일정이 동일한 concert_id 를 가질 수 있음)
- 변경 빈도: 낮음 (일정이 생성된 후 변경 가능성 낮음)

#### 복합 인덱스
1. `concert_id`, `reservation_at`, `deadline`
- 필요성: 예약 가능 날짜 조회 요청에서 자주 사용된다.
- cardinality: 중간
- 변경 빈도: 낮음 (생성 후 변경 가능성 낮음)

#### 인덱스 설정
```java
@Table(indexes = {
        @Index(name = "idx_concert_id", columnList = "concert_id"),
        @Index(name = "idx_concert_schedule_date", columnList = "concert_id, reservation_at, deadline")
})
public class ConcertScheduleEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "concert_id", nullable = false)
    private ConcertEntity concert;

    private LocalDateTime reservationAt;
    private LocalDateTime deadline;
    private LocalDateTime concertAt;
}
```

### seat
#### 단일 인덱스
1. `concert_schedule_id`
- 필요성: 대부분의 쿼리에서 `concert_schedule_id` 를 사용하여 seat 테이블을 조회한다.
- cardinality: 중간 (여러 좌석이 동일한 `schedule_id` 를 가질 수 있음)
- 변경 빈도: 낮음

#### 복합 인덱스
1. `concert_schedule_id`, `status`
- 필요성: 예약 가능 좌석 조회와 관련된 쿼리에서 `concert_schedule_id` 와 `status` 를 함께 사용하여 조회한다.
- cardinality: 중간
- 변경 빈도: 중간 (좌석의 예약 상태는 자주 변경될 수 있음)

#### 인덱스 설정
```java
@Table(indexes = {
                @Index(name = "idx_concert_schedule_id", columnList = "concert_schedule_id"),
                @Index(name = "idx_concert_schedule_status", columnList = "concert_schedule_id, status"),
})
public class SeatEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "concert_schedule_id", nullable = false)
    private ConcertScheduleEntity concertSchedule;

    private int seatNo;

    @Enumerated(value = EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime reservationAt;

    private int seatPrice;
}
```

### point
#### 단일 인덱스
1. `user_id`
- 필요성: 사용자 잔액 조회/충전/결제에서 빈번히 사용된다.
- cardinality: 높음 (사용자 ID는 PK 이므로 중복도가 매우 낮음)
- 변경 빈도: 낮음

#### 인덱스 설정
```java
@Table(indexes = @Index(name = "idx_point_user_id", columnList = "user_id"))
public class PointEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private UserEntity user;

    private Long amount;

    private LocalDateTime lastUpdatedAt;
}
```

### reservation
#### 단일 인덱스
1. `user_id`
- 필요성: 사용자와 관련된 모든 예약을 조회하는 데 사용될 수 있음 (현재 API 제공은 하지 않음)
- cardinality: 높음
- 변경 빈도: 낮음

2. `status`
- 필요성: 결제되지 않은 예약 건의 좌석 상태를 변경할 때 사용된다.
- cardinality: 중간 (여러 예약이 동일한 상태를 가질 수 있음)
- 변경 빈도: 중간

#### 복합 인덱스
1. `concert_id`, `concert_schedule_id`, `seat_id`
- 필요성: 특정 좌석에 대한 예약 내역을 조회할 때 사용된다.
- cardinality: 중간 (컬럼 조합으로 유일한 식별자가 된다)
- 변경 빈도: 낮음

#### 인덱스 설정
```java
@Table(indexes = {
        @Index(name = "idx_reservation_user_id", columnList = "user_id"),
        @Index(name = "idx_reservation_status", columnList = "status"),
        @Index(name = "idx_concert_schedule_seat_id", columnList = "concert_id, concert_schedule_id, seat_id")
})
public class ReservationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "concert_id")
    private ConcertEntity concert;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "concert_schedule_id")
    private ConcertScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservationAt;
}
```

### payment
- 현재 API 는 검색 조건이 존재하지 않기 때문에 인덱스는 불필요하다.
- `payment` 의 특성을 고려할 때, 향후 검색 요구가 발생할 가능성을 대비해볼 수 있다.

#### 단일 인덱스
1. `user_id`
- 필요성: 특정 사용자의 모든 결제 내역을 조회할 때 사용된다.
- cardinality: 높음
- 변경 빈도: 낮음 (결제 시 생성되어 이후 변경이 없음)

#### 인덱스 설정
```java
@Table(indexes = @Index(name = "idx_payment_user_id", columnList = "user_id"))
public class PaymentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private UserEntity user;

    private int amount;

    private LocalDateTime paymentAt;
}
```

## BEFORE & AFTER
> 인덱스 적용 전과 후 비교

### 예약 가능 날짜 조회
```sql
explain analyze
select
    cse1_0.id,
    cse1_0.concert_id,
    cse1_0.concert_at,
    cse1_0.deadline,
    cse1_0.reservation_at 
from
    concert_schedule cse1_0 
where
    cse1_0.concert_id=1
    and cse1_0.reservation_at<now()
    and cse1_0.deadline>now();
```
- Before
```text
'1', 'SIMPLE', 'cse1_0', NULL, 'ALL', NULL, NULL, NULL, NULL, '100062', '1.11', 'Using where'
```
```text
-> Filter: ((cse1_0.concert_id = 1) and (cse1_0.reservation_at < <cache>(now())) and (cse1_0.deadline > <cache>(now())))  (cost=9205 rows=1112) (actual time=0.413..56.6 rows=100000 loops=1)\n    
-> Table scan on cse1_0  (cost=9205 rows=100062) (actual time=0.285..39.2 rows=100000 loops=1)
```

- After
```text
'1', 'SIMPLE', 'cse1_0', NULL, 'ref', 'idx_concert_id,idx_concert_schedule_date', 'idx_concert_id', '8', 'const', '50031', '11.11', 'Using where'
```
```text
-> Filter: ((cse1_0.reservation_at < <cache>(now())) and (cse1_0.deadline > <cache>(now())))  (cost=821 rows=5558) (actual time=0.356..109 rows=100000 loops=1)  
-> Index lookup on cse1_0 using idx_concert_id (concert_id=1)  (cost=821 rows=50031) (actual time=0.343..99.4 rows=100000 loops=1)
```
#### 결과 분석
|항목	|Before	| After                                                             |
|-------|-----|-------------------------------------------------------------------|
|인덱스 사용|	없음 (FULL 스캔)	| idx_concert_id 인덱스를 사용하여 concert_id로 인덱스 룩업 수행 |
|필터링 단계	|테이블 스캔 후 concert_id와 concert_date에 대한 필터링 수행	| 인덱스 룩업 단계에서 concert_id와 reservation_at, deadline 조건을 만족하는 레코드를 효율적으로 검색       |
|비용	|9205	| 821                                                               |
|예상 행 수	|100062	| 50031                                                             |
|실제 실행 시간	|0.413 ms	| 0.356 ms                                                          |
|실제 처리된 행 수	|100000	| 100000                                                            |

### 예약 가능 좌석 조회
```sql
explain analyze
select
	se1_0.id,
	se1_0.concert_schedule_id,
	se1_0.reservation_at,
	se1_0.seat_no,
	se1_0.seat_price,
	se1_0.status 
from
	seat se1_0 
join
	concert_schedule cs1_0 
		on cs1_0.id=se1_0.concert_schedule_id 
where
	se1_0.concert_schedule_id=1
	and cs1_0.concert_id=1
	and se1_0.status='AVAILABLE'
```

- Before
```text
'1', 'SIMPLE', 'cs1_0', NULL, 'const', 'PRIMARY', 'PRIMARY', '8', 'const', '1', '100.00', NULL
'1', 'SIMPLE', 'se1_0', NULL, 'ALL', NULL, NULL, NULL, NULL, '50127', '5.00', 'Using where'
```
```text
-> Filter: ((se1_0.`status` = \'AVAILABLE\') and (se1_0.concert_schedule_id = 1))  (cost=5053 rows=2506) (actual time=0.165..37.9 rows=50000 loops=1)\n    -> Table scan on se1_0  (cost=5053 rows=50127) (actual time=0.159..23.7 rows=50000 loops=1)
```

- After
```text
'1', 'SIMPLE', 'cs1_0', NULL, 'const', 'PRIMARY,idx_concert_id,idx_concert_schedule_date', 'PRIMARY', '8', 'const', '1', '100.00', NULL
'1', 'SIMPLE', 'se1_0', NULL, 'ref', 'idx_concert_schedule_id,idx_concert_schedule_status', 'idx_concert_schedule_id', '8', 'const', '25063', '50.00', 'Using where'
```
```text
-> Filter: (se1_0.`status` = \'AVAILABLE\')  (cost=1374 rows=12532) (actual time=0.479..74.1 rows=50000 loops=1)   
-> Index lookup on se1_0 using idx_concert_schedule_id (concert_schedule_id=1)  (cost=1374 rows=25063) (actual time=0.475..65.2 rows=50000 loops=1)
```

#### 결과 분석
|항목	|Before	| After                                                             |
|-------|-------|-------------------------------------------------------------------|
|인덱스 사용|	PRIMARY 인덱스만 사용 (테이블 스캔 진행)	| PRIMARY, idx_concert_id, idx_concert_schedule_date 인덱스 사용 |
|필터링 단계	|테이블 스캔 후 status와 concert_schedule_id에 대한 조건 필터링 수행	| idx_concert_schedule_id 인덱스를 사용하여 먼저 concert_schedule_id 조건을 만족하는 행을 빠르게 찾고, status 필터링을 효율적으로 수행 |
|비용	|5053	| 1374                                                               |
|예상 행 수	|50127	| 25063 (인덱스 사용으로 concert_schedule_id=1에 맞는 예상 행 수 줄어듦)        |
|실제 실행 시간	|0.165 ms	| 0.479 ms                                                          |
|실제 처리된 행 수	|50000	| 50000                                                            |

### 포인트 조회/충전
```sql
explain analyze
select
  pe1_0.id,
  pe1_0.amount,
  pe1_0.last_updated_at,
  pe1_0.user_id 
from 
  point pe1_0
where 
  user_id=9999;
```

- Before
```text
'1', 'SIMPLE', 'pe1_0', NULL, 'ALL', NULL, NULL, NULL, NULL, '10036', '10.00', 'Using where'
```
```text
-> Filter: (pe1_0.user_id = 9999)  (cost=1028 rows=1004) (actual time=10.3..10.3 rows=1 loops=1)\n    
-> Table scan on pe1_0  (cost=1028 rows=10036) (actual time=0.28..9.09 rows=10000 loops=1)
```

- After
```text
'1', 'SIMPLE', 'pe1_0', NULL, 'const', 'user_id,idx_point_user_id', 'user_id', '9', 'const', '1', '100.00', NULL
```
```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=417e-6..459e-6 rows=1 loops=1)
```
#### 결과 분석
|항목	| Before	                              | After                                                 |
|-------|--------------------------------------|-------------------------------------------------------|
|인덱스 사용| 	ALL (전체 테이블 스캔)	                    | user_id와 idx_point_user_id 인덱스 사용                     |
|필터링 단계	| 테이블 스캔 후 user_id=9999 조건에 맞는 행을 필터링	 | user_id 인덱스를 사용하여 user_id=9999에 맞는 레코드를 빠르게 찾고 필터링 수행 |
|비용	| 1028	                                | 0                                                     |
|예상 행 수	| 10036	                               | 1                                                     |
|실제 실행 시간	| 10.3 ms	                             | 0.459 ms                                              |
|실제 처리된 행 수	| 1	                                   | 1                                                     |

### 좌석 예약
```sql
explain analyze
select 
  cse1_0.id,
  cse1_0.concert_id,
  cse1_0.concert_at,
  cse1_0.deadline,
  cse1_0.reservation_at 
from 
  concert_schedule cse1_0 
where 
  cse1_0.id=1;
```

- Before
```text
'1', 'SIMPLE', 'cse1_0', NULL, 'const', 'PRIMARY', 'PRIMARY', '8', 'const', '1', '100.00', NULL
```
```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=541e-6..625e-6 rows=1 loops=1)
```

- After
```text
'1', 'SIMPLE', 'cse1_0', NULL, 'const', 'PRIMARY', 'PRIMARY', '8', 'const', '1', '100.00', NULL
```
```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=416e-6..499e-6 rows=1 loops=1)
```
#### 결과 분석
차이 없음

```sql
explain analyze
select 
  se1_0.id,
  se1_0.concert_schedule_id,
  se1_0.reservation_at,
  se1_0.seat_no,
  se1_0.seat_price,
  se1_0.status 
from 
  seat se1_0 
where 
  se1_0.id=1;
```

- Before
```text
'1', 'SIMPLE', 'se1_0', NULL, 'const', 'PRIMARY', 'PRIMARY', '8', 'const', '1', '100.00', NULL
```
```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=375e-6..417e-6 rows=1 loops=1)
```

- After
```text
'1', 'SIMPLE', 'se1_0', NULL, 'const', 'PRIMARY', 'PRIMARY', '8', 'const', '1', '100.00', NULL
```
```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=542e-6..625e-6 rows=1 loops=1)
```
#### 결과 분석
차이 없음