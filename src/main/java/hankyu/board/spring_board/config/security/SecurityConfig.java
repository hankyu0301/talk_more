package hankyu.board.spring_board.config.security;

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

                .and()
                .authorizeRequests()
                .antMatchers("/api/sign-in", "/api/sign-up","/api/token-reissue").permitAll()
                .antMatchers(HttpMethod.GET, "/image/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/members/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/members/{id}").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/members/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/categories").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/categories/{id}").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/api/posts").authenticated()
                .antMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .antMatchers(HttpMethod.GET, "/api/posts/{id}").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/{id}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/posts/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/comments").permitAll()
                .antMatchers(HttpMethod.POST, "/api/comments").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comments/{id}").authenticated()

                .antMatchers(HttpMethod.GET, "/api/messages/sent", "/api/messages/received").authenticated()
                .antMatchers(HttpMethod.GET, "/api/messages/{id}").authenticated()
                .antMatchers(HttpMethod.POST, "/api/messages").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/messages/sender").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/messages/receiver").authenticated()

                .anyRequest().hasAnyRole("ADMIN");


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/exception/**",
                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
    }
}
