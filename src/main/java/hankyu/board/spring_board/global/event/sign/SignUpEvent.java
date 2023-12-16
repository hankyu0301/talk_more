package hankyu.board.spring_board.global.event.sign;

import hankyu.board.spring_board.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpEvent {
    private MemberDto createdMember;
}
