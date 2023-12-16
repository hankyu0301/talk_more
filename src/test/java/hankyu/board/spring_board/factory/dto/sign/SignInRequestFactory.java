package hankyu.board.spring_board.factory.dto.sign;


import hankyu.board.spring_board.global.dto.sign.SignInRequest;

public class SignInRequestFactory {
    public static SignInRequest createSignInRequest(){
        return new SignInRequest("finebears@naver.com", "123456a!");
    }
}
