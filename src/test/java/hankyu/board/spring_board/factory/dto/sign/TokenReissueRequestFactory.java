package hankyu.board.spring_board.factory.dto.sign;

import hankyu.board.spring_board.dto.token.TokenReissueRequest;

public class TokenReissueRequestFactory {
    public static TokenReissueRequest createTokenReissueRequest() {
        return new TokenReissueRequest("access", "refresh");
    }
}
