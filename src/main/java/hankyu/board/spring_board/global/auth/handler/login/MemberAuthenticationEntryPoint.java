package hankyu.board.spring_board.global.auth.handler.login;

import hankyu.board.spring_board.global.auth.error.AuthenticationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {
        log.error("### MemberAuthenticationEntryPoint Error!! : " + authException.getMessage());
        AuthenticationError.setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "인증에 실패했습니다.", response);
    }
}
