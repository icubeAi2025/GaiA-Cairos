package kr.co.ideait.platform.gaiacairos.core.util;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil {
	
	private final RedisTemplate<String, String> redisTemplate;

    public void saveRedis(String key, String value) {
    	Duration expiredTime = Duration.ofDays(1); // 24시간
    	saveRedis(key, value, expiredTime);
    }
    
    public void saveRedis(String key, String value, Duration expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime);
    }

    public Object getRedisValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRedis(String key) {
        redisTemplate.delete(key);
    }
    
    public Long getExpire(String key) {
    	return redisTemplate.getExpire(key);
    }
}
