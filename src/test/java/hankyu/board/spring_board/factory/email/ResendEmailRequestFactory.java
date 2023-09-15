package hankyu.board.spring_board.factory.email;

import hankyu.board.spring_board.dto.email.ResendEmailRequest;

public class ResendEmailRequestFactory {
    public static ResendEmailRequest createResendEmailRequest(){
        return new ResendEmailRequest("finebears@naver.com");
    }

    public static ResendEmailRequest createResendEmailRequestWithEmail(String email){
        return new ResendEmailRequest(email);
    }
}
