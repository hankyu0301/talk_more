package hankyu.board.spring_board.factory.dto.sign;


import hankyu.board.spring_board.global.dto.sign.SignUpRequest;

public class SignUpRequestFactory {

    public static SignUpRequest createSignUpRequest() {
        return new SignUpRequest("finebears@naver.com", "123456a!", "장한규", "finebears");
    }

}