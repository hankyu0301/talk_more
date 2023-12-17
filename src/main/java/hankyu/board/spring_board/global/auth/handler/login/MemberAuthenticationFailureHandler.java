package hankyu.board.spring_board.global.auth.handler.login;

import hankyu.board.spring_board.global.auth.error.AuthenticationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        log.error("### Authentication failed: {}", exception.getMessage());
        log.error("### Authentication failed: {}", exception.getClass().getName());
        AuthenticationError.setErrorResponse(HttpStatus.BAD_REQUEST, 400, "로그인에 실패하였습니다.", response);
    }


}
