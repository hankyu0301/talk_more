package hankyu.board.spring_board.global.config.security;

import hankyu.board.spring_board.global.jwt.JwtAccessDeniedHandler;
import hankyu.board.spring_board.global.jwt.JwtAuthenticationEntryPoint;
import hankyu.board.spring_board.global.jwt.JwtSecurityConfig;
import hankyu.board.spring_board.global.jwt.TokenProvider;
import hankyu.board.spring_board.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/sign-in", "/api/sign-up","/api/log-out").permitAll()
                .antMatchers(HttpMethod.POST, "/api/log-out","/api/token").authenticated()

                .antMatchers(HttpMethod.GET, "/images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/members/{id}").permitAll()
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

                .antMatchers(HttpMethod.GET, "/api/messages/sender").authenticated()
                .antMatchers(HttpMethod.GET, "/api/messages/receiver").authenticated()
                .antMatchers(HttpMethod.GET, "/api/messages/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/api/messages").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/messages/sender").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/messages/receiver").authenticated()

                .anyRequest().hasAnyRole("ADMIN")
                .and()

                .apply(new JwtSecurityConfig(tokenProvider, redisService));


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/exception/**",
                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
    }
}
