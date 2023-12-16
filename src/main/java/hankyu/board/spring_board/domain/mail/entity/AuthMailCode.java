package hankyu.board.spring_board.domain.mail.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Builder
@AllArgsConstructor
@Getter
@RedisHash(value = "authMailCode", timeToLive = 60 * 10)
public class AuthMailCode {
    @Id
    private String id;
    private String authCode;
    @Indexed
    private String email;
}