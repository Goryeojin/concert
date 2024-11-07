# Caching 기법 활용

## 1. 요구사항
> 조회가 오래 걸리는 쿼리에 대한 캐싱, 혹은 Redis 를 이용한 로직 이관을 통해 성능 개선할 수 있는 로직을 분석   

## 2. 분석
### 1. 콘서트 조회 API (`GET /api/v1/concerts`)
#### 설명
- 콘서트 목록을 조회한다.

#### 분석
- **조회 빈도**: 콘서트 예약 시스템에서 콘서트 조회는 메인 기능이라 자주 조회된다.
- **변경 빈도**: 콘서트 데이터가 추가되거나 수정되는 빈도는 낮다.

#### 선택
1. 캐싱 전략
   - Look-aside 캐싱 선택
   - 콘서트 조회는 읽기 빈도가 높고, 데이터 변경 빈도는 낮다.
2. 캐시 유형
    - 글로벌 캐시 선택
    - 서로 다른 여러 사용자로부터 동일하게 조회될 가능성이 높다.

### 2. 예약 가능한 콘서트 일정 조회 API (`GET /api/v1/concerts/{concertId}/schedules`)
#### 설명
- 특정 콘서트의 예약 가능한 날짜를 조회한다.

#### 분석
- **조회 빈도**: 예약 시 자주 조회된다.
- **변경 빈도**: 일정은 자주 변경되지 않으므로 데이터 변경 빈도는 낮다.

#### 선택
1. 캐싱 전략
   - Look-aside 캐싱 선택
   - 예약 가능한 날짜 조회는 읽기 빈도가 높고, 데이터 변경 빈도는 낮다.
2. 캐시 유형
    - 글로벌 캐시 선택
    - 서로 다른 여러 사용자로부터 동일하게 조회될 가능성이 높다.

### 3. 예약 가능한 좌석 조회 API (`GET /api/v1/concerts/{concertId}/schedules/{scheduleId}/seats`)
#### 설명
- 특정 콘서트 일정에 대해 예약 가능한 좌석을 조회한다.

#### 분석
- **조회 빈도**: 예약 시 자주 조회된다.
- **변경 빈도**: 예약 상황에 따라 변경이 발생할 수 있어 데이터 변경 빈도는 중간 정도이다.

#### 선택
1. 캐싱 전략
   - Look-aside 캐싱 선택
   - 예약 가능한 좌석 조회는 읽기 빈도가 높고, 데이터 변경 빈도는 중간이다.
2. 캐시 유형
    - 글로벌 캐시
    - 서로 다른 여러 사용자로부터 동일하게 조회될 가능성이 높고, 자주 변경되는 만큼 데이터 일관성이 중요하다.

### 4. 포인트 조회 API (`GET /api/v1/users/{userId}/point`)
#### 설명
- 특정 사용자가 포인트 잔액을 조회한다.

#### 분석
- **조회 빈도**: 결제나 충전 시 잔액을 자주 조회한다.
- **변경 빈도**: 결제나 충전으로 변경이 발생할 수 있어 데이터 변경 빈도는 중간이다.

#### 선택
1. 캐싱 전략
   - Look-aside 캐싱 선택
   - 읽기 빈도, 데이터 변경 빈도는 중간 정도이다.
2. 캐시 유형
    - 로컬 캐시
    - 사용자별로 개인화된 데이터를 조회하므로, 전역 자원에서 데이터를 관리하기보다는 각 인스턴스에서 데이터를 관리하는 로컬 캐시를 사용하는 것이 적합하다.
    - 특정 사용자에 대한 집중적인 요청이 발생하는 상황이나 단기간의 부하와 같은 특수 케이스에 대한 방어 목적에 초점을 맞춘다.
    - 일관성을 유지하면서도 효율적인 캐싱을 위해 짧은 TTL 을 설정한다.

### 5. 대기열 상태 조회 API (`GET /api/v1/queue/status`)
#### 설명
- 특정 사용자의 대기열 상태를 조회한다.

#### 분석
- **조회 빈도**: 대기 중인 사용자가 자주 조회한다.
- **변경 빈도**: 대기열 상태는 자주 변경되므로 데이터 변경 빈도는 매우 높다.

#### 선택
1. 캐싱 전략
   - Look-aside 캐싱 선택
   - 읽기 빈도, 데이터 변경 빈도가 높다. (다른 캐싱 전략 선택하기에 시간이 없어 모두 Look-aside 로 우선 진행하고 대기열 조회 같은 경우에는 ttl 을 5초만 가져갔습니다.)
2. 캐시 유형
    - 로컬 캐시
    - 사용자별로 개인화된 데이터를 조회하므로 로컬 캐시를 사용하는 것이 적합하다.
    - 일관성을 유지하면서도 효율적인 캐싱을 위해 짧은 TTL 을 설정한다.

## 구현
### `CacheConfig.java`
```java
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    @Primary
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration(Duration.ofMinutes(10))) // (1)
                .withInitialCacheConfigurations(Map.of(
                        "shortLivedCache", redisCacheConfiguration(Duration.ofSeconds(5) // (2)
                )))
                .build();
    }

    private RedisCacheConfiguration redisCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(customObjectMapper())
                ));
    }

    @Bean
    // 로컬 캐시 설정
    public CaffeineCacheManager caffeineCacheManager() { // (3)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .maximumSize(5000));
        return cacheManager;
    }

    @Bean
    public ObjectMapper customObjectMapper() { // (4)
        return new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
    }
}
```

1. `redisCacheManager`
   - 기본적으로 사용할 글로벌 캐시 설정을 적용했다.
   - 콘서트, 일정 조회 시 사용할 목적으로 TTL 을 10분으로 설정하였다.
2. `shortLivedCache`
   - 짧은 주기로 캐싱해야 하는 경우를 위해 추가했다.
   - 좌석 조회 시 사용할 목적으로 TTL 을 5초로 설정하였다.
3. `caffeineCacheManager`
   - 각 인스턴스에서 짧은 주기로 캐싱해야 하는 경우를 위해 설정한 github 캐시 매니저이다.
   - 포인트 조회, 대기열 상태 조회 시 사용할 목적으로 TTL 을 5초로 설정하였다.
4. `customObjectMapper`
   - ObjectMapper 를 설정하였다.