package hankyu.board.spring_board.factory.dto.member;

import hankyu.board.spring_board.dto.member.ResendEmailRequest;

public class ResendEmailRequestFactory {
    public static ResendEmailRequest createResendEmailRequest(){
        return new ResendEmailRequest("finebears@naver.com");
    }

    public static ResendEmailRequest createResendEmailRequestWithEmail(String email){
        return new ResendEmailRequest(email);
    }
}
