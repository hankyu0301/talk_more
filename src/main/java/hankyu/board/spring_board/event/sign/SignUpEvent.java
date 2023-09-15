package hankyu.board.spring_board.event.sign;

import hankyu.board.spring_board.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpEvent {
    private MemberDto createdMember;
}
