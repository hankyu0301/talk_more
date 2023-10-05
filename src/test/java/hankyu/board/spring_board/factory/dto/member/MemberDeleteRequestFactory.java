package hankyu.board.spring_board.factory.dto.member;

import hankyu.board.spring_board.dto.member.MemberDeleteRequest;

public class MemberDeleteRequestFactory {
    public static MemberDeleteRequest createMemberDeleteRequest() {
        return new MemberDeleteRequest("accessToken");
    }
}
