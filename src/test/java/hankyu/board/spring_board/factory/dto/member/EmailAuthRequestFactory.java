package hankyu.board.spring_board.factory.dto.member;

import hankyu.board.spring_board.dto.member.EmailConfirmRequest;

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
