package hankyu.board.spring_board.global.config.security;

import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.oauth.service.KakaoTokenOauthService;
import hankyu.board.spring_board.global.auth.handler.login.MemberAuthenticationEntryPoint;
import hankyu.board.spring_board.global.auth.handler.logout.MemberLogoutHandler;
import hankyu.board.spring_board.global.auth.handler.logout.MemberLogoutSuccessHandler;
import hankyu.board.spring_board.global.auth.handler.oauth.OAuth2SuccessHandler;
import hankyu.board.spring_board.global.auth.jwt.DelegateTokenUtil;
import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.auth.userdetails.CustomOAuth2UserService;
import hankyu.board.spring_board.global.auth.utils.AccessTokenRenewalUtil;
import hankyu.board.spring_board.global.auth.utils.OAuth2TokenUtils;
import hankyu.board.spring_board.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;
    private final AccessTokenRenewalUtil accessTokenRenewalUtil;
    private final DelegateTokenUtil delegateTokenUtil;
    private final RedisService redisService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2TokenUtils oAuth2TokenUtils;
    private final KakaoTokenOauthService kakaoTokenOauthService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                .logoutUrl("/api/users/logout")
                .deleteCookies("Refresh")
                .addLogoutHandler(new MemberLogoutHandler(redisService, jwtTokenizer))
                .logoutSuccessHandler(new MemberLogoutSuccessHandler())
                .and()
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint()
                        .userService(oAuth2UserService)
                        .and()
                        .successHandler(
                                new OAuth2SuccessHandler(delegateTokenUtil, memberRepository, jwtTokenizer,
                                        oAuth2TokenUtils, kakaoTokenOauthService)))

                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/users/logout", "/api/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/token").authenticated()

                .antMatchers(HttpMethod.GET, "/api/**", "/images/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/members/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/api/members").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/members/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/members/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/categories").permitAll()
                .antMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/categories/{id}").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/api/email").permitAll()
                .antMatchers(HttpMethod.POST, "/api/email").permitAll()

                .antMatchers(HttpMethod.GET, "/api/posts").permitAll()
                .antMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/posts/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/comments").permitAll()
                .antMatchers(HttpMethod.POST, "/api/comments").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/messages/sender","/api/messages/receiver", "/api/messages/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/api/messages").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/messages/sender", "/api/messages/receiver").authenticated()

                .anyRequest().permitAll()
                .and()

                .apply(jwtSecurityConfig());


        return http.build();
    }


    @Bean
    public JwtSecurityConfig jwtSecurityConfig() {
        return new JwtSecurityConfig(jwtTokenizer, delegateTokenUtil, accessTokenRenewalUtil, redisService);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/exception/**",
                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(
                List.of());
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refresh");
        configuration.addExposedHeader("Location");
        configuration.addExposedHeader("Set-Cookie");
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
