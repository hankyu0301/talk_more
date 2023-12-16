package hankyu.board.spring_board.factory.entity.mail;

import hankyu.board.spring_board.domain.mail.entity.AuthMailCode;

public class AuthMailCodeFactory {

    public static AuthMailCode createAuthMailCode() {
        return new AuthMailCode("1",
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfQURNSU4iLCJleHAiOjE3MDI3OTM5NTR9.MeEAEIUDpuME9s8ZvBKH3K932heDQ6iI9dMQOWyzKljQjTq_IzehZj3XIzTR7odLuNiJib5Ict5-zjyZeU10XQ",
                "finebears@naver.com");
    }
}
