package hhplus.concert.infra.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Long getSize(String key) {
        // "activeToken:"로 시작하는 모든 키를 가져옴
        Set<String> keys = redisTemplate.keys("activeToken:*");

        // 키가 null이 아니면 키의 개수를 반환
        return (long) (keys != null ? keys.size() : 0);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public void addSortedSet(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Long getSortedSetSize(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    public Set<Object> getSortedSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public void removeSortedSetMembers(String key, Set<Object> values) {
        redisTemplate.opsForZSet().remove(key, values.toArray());
    }

    public Long getSortedSetRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }
}
