package hankyu.board.spring_board.factory.dto.member;

import hankyu.board.spring_board.dto.member.MemberUpdateRequest;

public class MemberUpdateReuqestFactory {

    public static MemberUpdateRequest createMemberUpdateRequest() {
        return new MemberUpdateRequest("newUsername","newNickname");
    }
}
