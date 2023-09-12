package hankyu.board.spring_board.dto.member;

import hankyu.board.spring_board.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String email;
    private String nickname;
    private String username;

    public static MemberDto toDto(Member member) {
        return new MemberDto(member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getUsername());
    }
}
