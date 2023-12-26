package hankyu.board.spring_board.global.auth.handler.oauth;

import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.oauth.service.KakaoTokenOauthService;
import hankyu.board.spring_board.global.auth.jwt.DelegateTokenUtil;
import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.auth.userdetails.OAuthAttributes;
import hankyu.board.spring_board.global.auth.utils.OAuth2TokenUtils;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("{elastic.ip.address}")
    private String address;

    private final DelegateTokenUtil delegateTokenUtil;
    private final MemberRepository memberRepository;
    private final JwtTokenizer jwtTokenizer;
    private final OAuth2TokenUtils oAuth2TokenUtils;
    private final KakaoTokenOauthService kakaoTokenOauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuthAttributes oAuth2User = (OAuthAttributes)authentication.getPrincipal();
        //OAuthAttributes 객체로 부터 Resource Owner의 이메일 주소를 얻어 회원이 존재하는지 확인
        Member findMember = memberRepository.findByEmail(oAuth2User.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2TokenUtils.getOAuth2AuthorizedClient(authentication);
        if (oAuth2TokenUtils.getOAuthRegistration(oAuth2AuthorizedClient).equals("kakao")) {
            String accessTokenValue = oAuth2TokenUtils.getOAuthAccessToken(oAuth2AuthorizedClient);
            String refreshTokenValue = oAuth2TokenUtils.getOAuthRefreshToken(oAuth2AuthorizedClient);
            kakaoTokenOauthService.saveOrUpdateToken(accessTokenValue, refreshTokenValue, findMember);
        }
        redirect(request, response, findMember);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, Member member) throws IOException {

        String accessToken = delegateTokenUtil.delegateAccessToken(member);
        String refreshToken = delegateTokenUtil.delegateRefreshToken(member);

        String uri = createURI(accessToken, refreshToken).toString();
        log.info("## OAuth2 로그인 성공! 토큰을 발급합니다. 해당 주소로 보낼게용 " + uri);
        jwtTokenizer.setHeaderAccessToken(response, accessToken);
        jwtTokenizer.setHeaderRefreshToken(response, refreshToken);
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host(address)
                .port(8080)// 리다이렉트 시킬 클라이언트 주소
                // .scheme("http")
                // .host("localhost")
                // .port(5173)
                .path("/oauth")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
