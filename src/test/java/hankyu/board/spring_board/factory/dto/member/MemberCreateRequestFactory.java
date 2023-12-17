package hankyu.board.spring_board.factory.dto.member;

import hankyu.board.spring_board.domain.member.dto.MemberCreateRequest;

public class MemberCreateRequestFactory {

    public static MemberCreateRequest createMemberCreateRequest() {
        return new MemberCreateRequest("finebears@naver.com","123456a!","finebears","jang");
    }
}
