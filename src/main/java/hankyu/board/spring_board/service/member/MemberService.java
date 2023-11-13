package hankyu.board.spring_board.service.member;

import hankyu.board.spring_board.auth.AuthChecker;
import hankyu.board.spring_board.dto.member.MemberDto;
import hankyu.board.spring_board.dto.member.MemberUpdateRequest;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthChecker authChecker;

    @Transactional(readOnly = true)
    public MemberDto findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return MemberDto.toDto(member);
    }

    @Transactional
    public void update(Long id, MemberUpdateRequest req) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        authChecker.authorityCheck(member.getId());
        validateMemberUpdateRequest(req);
        member.update(req);
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        authChecker.authorityCheck(member.getId());
        memberRepository.delete(member);
    }

    private void validateMemberUpdateRequest(MemberUpdateRequest req) {
        if(memberRepository.existsByNickname(req.getNickname())) {
            throw new DuplicateNicknameException(req.getNickname());
        }
    }

}
