package hankyu.board.spring_board.factory.dto.email;

import hankyu.board.spring_board.dto.email.EmailConfirmRequest;

public class EmailAuthRequestFactory {

    public static EmailConfirmRequest createEmailAuthRequest() {
        return new EmailConfirmRequest(
                "finebears@naver.com",
                "validCode"
        );
    }

    public static EmailConfirmRequest createEmailAuthRequestWithInvalidCode() {
        return new EmailConfirmRequest(
                "finebears@naver.com",
                "invalidCode"
        );
    }
}
