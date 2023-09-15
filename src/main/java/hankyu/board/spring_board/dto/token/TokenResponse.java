package hankyu.board.spring_board.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}

