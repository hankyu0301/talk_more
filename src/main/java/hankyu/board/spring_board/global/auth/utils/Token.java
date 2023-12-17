package hankyu.board.spring_board.global.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Token {
    private String accessToken;
    private String refreshToken;
}
