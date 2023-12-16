package hankyu.board.spring_board.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.global.dto.response.Response;
import hankyu.board.spring_board.global.exception.sign.*;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenInvalidSecretKeyException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰 비밀키 입니다.", request, response);
        } catch (MalformedJwtException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰 값 입니다.", request, response);
        } catch (ExpiredTokenException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "만료된 토큰입니다.", request, response);
        } catch (AlreadyLoggedOutAccessTokenException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "이미 로그아웃 처리된 토큰입니다.", request, response);
        } catch (InvalidRefreshTokenException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다.", request, response);
        } catch (AccessDeniedException | UnauthorizedTokenException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, 403, "접근이 거부 되었습니다.", request, response);
        }
    }

    public void setErrorResponse(HttpStatus status, int code, String msg, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(
                Response.failure(code, msg)
        ));
    }
}
