package hankyu.board.spring_board.global.config.security;

import hankyu.board.spring_board.global.auth.filter.JwtAuthenticationFilter;
import hankyu.board.spring_board.global.auth.filter.JwtVerificationFilter;
import hankyu.board.spring_board.global.auth.handler.login.MemberAuthenticationFailureHandler;
import hankyu.board.spring_board.global.auth.handler.login.MemberAuthenticationSuccessHandler;
import hankyu.board.spring_board.global.auth.jwt.DelegateTokenUtil;
import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.auth.utils.AccessTokenRenewalUtil;
import hankyu.board.spring_board.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtSecurityConfig extends AbstractHttpConfigurer<JwtSecurityConfig, HttpSecurity> {
    private final JwtTokenizer jwtTokenizer;
    private final DelegateTokenUtil delegateTokenUtil;
    private final AccessTokenRenewalUtil accessTokenRenewalUtil;
    private final RedisService redisService;

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager = builder
                .getSharedObject(AuthenticationManager.class);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager,
                delegateTokenUtil,
                jwtTokenizer);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/users/login");
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
        jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, accessTokenRenewalUtil,
                redisService);

        builder
                .addFilter(jwtAuthenticationFilter)
                .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
    }
}