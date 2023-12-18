package hankyu.board.spring_board.global.auth.utils;

import hankyu.board.spring_board.global.exception.common.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final String ROLE_ADMIN = "ROLE_ADMIN";

    public void authorityCheck(Long memberId) {
        if (hasRole() || (memberId.equals(getMemberId()))) {

        }   else {
            throw new UnauthorizedAccessException();
        }
    }

    private boolean hasRole() {
        return getMemberRoles().contains(ROLE_ADMIN);
    }

    private List<String> getMemberRoles() {
        return getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .collect(Collectors.toList());
    }

    public Long getMemberId() {
        return Long.valueOf(getAuthentication().getName());
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
