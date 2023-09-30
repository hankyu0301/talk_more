package hankyu.board.spring_board.aop;

import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.member.MemberRole;
import hankyu.board.spring_board.exception.common.UnauthorizedAccessException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthChecker {
    private final MemberRole ROLE_ADMIN = MemberRole.ROLE_ADMIN;
    private final MemberRepository memberRepository;

    public void authorityCheck(Member member) {
        Member loginMember = memberRepository.findById(getMemberId()).orElseThrow(MemberNotFoundException::new);
        if (!(hasRole() || member.equals(loginMember))) {
            throw new UnauthorizedAccessException();
        }
    }

    public boolean hasRole() {
        return getMemberRoles().stream().anyMatch(memberRole -> memberRole == ROLE_ADMIN);
    }

    public List<MemberRole> getMemberRoles() {
        return getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .map(MemberRole::valueOf)
                .collect(Collectors.toList());
    }

    public Long getMemberId() {
        return Long.valueOf(getAuthentication().getName());
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
