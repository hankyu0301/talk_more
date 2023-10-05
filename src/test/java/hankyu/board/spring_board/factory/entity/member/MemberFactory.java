package hankyu.board.spring_board.factory.entity.member;

import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.member.MemberRole;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFactory {

    public static Member createMember() {
        return new Member("finebears@naver.com", "123456a!", "장한규","finebears");
    }

    public static Member createMember(String email, String password, String username, String nickname) {
        return new Member("finebears@naver.com", "123456a!", "장한규","finebears");
    }

    public static Member createMemberWithId(Long id) {
        Member member = new Member("email@email.com", "123456a!", "nickname", "username");
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Member createAdminMemberWithId(Long id) {
        Member member = new Member("admin@admin.com", "123456a!", "admin", "username");
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.ROLE_ADMIN);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

}
