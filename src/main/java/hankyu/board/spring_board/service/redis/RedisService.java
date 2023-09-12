package hankyu.board.spring_board.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public String getData(RedisKey redisKey, String key) {
        return (String) redisTemplate.opsForValue().get(redisKey.getKey() + key);
    }

    public void setDataWithExpiration(RedisKey redisKey, String key, String value, Long time) {
        if (this.getData(redisKey, key) != null)
            this.deleteData(redisKey, key);
        Duration expireDuration = Duration.ofSeconds(time);
        redisTemplate.opsForValue().set(redisKey.getKey() + key, value, expireDuration);
    }

    public void deleteData(RedisKey redisKey,String key) {
        redisTemplate.delete(redisKey.getKey() + key);
    }

}