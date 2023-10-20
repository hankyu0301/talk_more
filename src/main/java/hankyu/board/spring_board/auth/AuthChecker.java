package hankyu.board.spring_board.auth;

import hankyu.board.spring_board.entity.member.MemberRole;
import hankyu.board.spring_board.exception.common.UnauthorizedAccessException;
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

    public void authorityCheck(Long memberId) {
        if (!hasRole() || !(memberId.equals(getMemberId()))) {
            throw new UnauthorizedAccessException();
        }
    }

    private boolean hasRole() {
        return getMemberRoles().stream().anyMatch(memberRole -> memberRole == ROLE_ADMIN);
    }

    private List<MemberRole> getMemberRoles() {
        return getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .map(MemberRole::valueOf)
                .collect(Collectors.toList());
    }

    public Long getMemberId() {
        return Long.valueOf(getAuthentication().getName());
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
