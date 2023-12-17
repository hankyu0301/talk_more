package hankyu.board.spring_board.global.auth.handler.logout;

import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.redis.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class MemberLogoutHandler implements LogoutHandler {

    private final RedisService redisService;
    private final JwtTokenizer jwtTokenizer;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String accessToken = jwtTokenizer.getHeaderAccessToken(request);
            String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
            Date expiration = jwtTokenizer.getClaims(accessToken, base64EncodedSecretKey).getBody().getExpiration();
            Long now = new Date().getTime();
            //TODO : 유효 시간
            redisService.setBlackList(accessToken, "accessToken", expiration.getTime() - now);
        } catch (ExpiredJwtException eje) {
            log.error("### 토큰이 만료되었습니다. 그대로 로그아웃합니다.");
        }

    }
}
