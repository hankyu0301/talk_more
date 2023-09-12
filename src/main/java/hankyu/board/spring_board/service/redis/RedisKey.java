package hankyu.board.spring_board.service.redis;

import lombok.Getter;

@Getter
public enum RedisKey {
    EMAIL("Email : "), REFRESH_TOKEN("RefreshToken : "), BLACK_LIST("BlackList : ");

    private String key;

    RedisKey(String key) {
        this.key = key;
    }
}