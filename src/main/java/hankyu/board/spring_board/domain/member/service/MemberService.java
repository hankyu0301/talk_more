package hankyu.board.spring_board.domain.member.service;

import hankyu.board.spring_board.domain.member.dto.MemberDto;
import hankyu.board.spring_board.domain.member.dto.MemberUpdateRequest;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.global.auth.AuthChecker;
import hankyu.board.spring_board.global.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
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
