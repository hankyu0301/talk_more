package hankyu.board.spring_board.domain.token.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@AllArgsConstructor
@Getter
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 3)
public class RefreshToken {
    @Id
    private String id;
    private String refreshToken;
    @Indexed
    private String accessToken;
}

