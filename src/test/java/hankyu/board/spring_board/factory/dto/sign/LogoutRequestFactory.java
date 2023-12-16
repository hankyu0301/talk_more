package hankyu.board.spring_board.factory.dto.sign;

import hankyu.board.spring_board.global.dto.sign.LogoutRequest;

public class LogoutRequestFactory {

    public static LogoutRequest createLogoutRequest() {
        return new LogoutRequest("accessToken", "refreshToken");
    }
}
