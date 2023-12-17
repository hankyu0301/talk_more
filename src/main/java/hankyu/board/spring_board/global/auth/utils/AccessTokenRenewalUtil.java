package hankyu.board.spring_board.global.auth.utils;

import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.global.auth.jwt.DelegateTokenUtil;
import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.token.RefreshTokenNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class AccessTokenRenewalUtil {
    private final MemberRepository memberRepository;
    private final DelegateTokenUtil delegateTokenUtil;
    private final JwtTokenizer jwtTokenizer;

    public Token renewAccessToken(HttpServletRequest request) {
        try {
            String refreshToken = jwtTokenizer.getHeaderRefreshToken(request);
            String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
            String email = jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey).getBody().getSubject();
            Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
            String newAccessToken = delegateTokenUtil.delegateAccessToken(member);
            return Token.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
        } catch (RefreshTokenNotFoundException | ExpiredJwtException e) {
            throw e;
        }
    }

}