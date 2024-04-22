package hankyu.board.spring_board.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.global.auth.dto.LoginDto;
import hankyu.board.spring_board.global.auth.jwt.DelegateTokenUtil;
import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.exception.common.JsonParsingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final DelegateTokenUtil delegateTokenUtil;
    private final JwtTokenizer jwtTokenizer;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto;
        try {
            loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new JsonParsingException(e.getMessage());
        }
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult) throws ServletException, IOException {
        Member member = (Member)authResult.getPrincipal();
        String accessToken = delegateTokenUtil.delegateAccessToken(member);
        String refreshToken = delegateTokenUtil.delegateRefreshToken(member);

        jwtTokenizer.setHeaderAccessToken(response, accessToken);
        jwtTokenizer.setHeaderRefreshToken(response, refreshToken);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }
}
