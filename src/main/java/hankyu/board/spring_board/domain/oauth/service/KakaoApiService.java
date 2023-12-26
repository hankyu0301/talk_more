package hankyu.board.spring_board.domain.oauth.service;

import com.google.gson.Gson;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.oauth.entity.KakaoToken;
import hankyu.board.spring_board.domain.oauth.repository.KakaoTokenRepository;
import hankyu.board.spring_board.domain.oauth.template.KakaoTemplate;
import hankyu.board.spring_board.domain.oauth.template.KakaoTemplateConstructor;
import hankyu.board.spring_board.global.exception.token.AccessTokenNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final KakaoTokenRepository kakaoTokenRepository;
    private final KakaoTokenOauthService kakaoTokenOauthService;
    private final KakaoTemplateConstructor kakaoTemplateConstructor;
    private final Gson gson;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String apiKey;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String apiSecret;

    private final String messageApiUrl = "https://kapi.kakao.com/v2/api/talk/memo/send";
    private final String tokenRenewalApiUri = "https://kauth.kakao.com/oauth/token";
    private final String unlinkKakaoApiUri = "https://kapi.kakao.com/v1/user/unlink";

    @Async
    public void sendMessage(Object template, String accessToken) {
        WebClient.create(messageApiUrl)
                .post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + accessToken)
                .body(BodyInserters.fromFormData("template_id", "102211"))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, // 4xx 에러인 경우
                        clientResponse -> {
                            renewKakaoAccessTokenAndResend(template, accessToken);
                            return Mono.empty();
                        })
                .bodyToMono(String.class)
                .subscribe(
                        result -> {
                            // 메시지 전송 성공 시 처리
                            log.info("Message sent successfully: " + result);
                        },
                        error -> {
                            // 에러 처리
                            log.error("Failed to send message", error);
                            error.printStackTrace();
                        }
                );  // 메시지 전송
    }

    private void renewKakaoAccessTokenAndResend(Object template, String accessToken) {
        KakaoToken kakaoToken = kakaoTokenRepository.findByAccessToken(accessToken)
            .orElseThrow(AccessTokenNotFound::new);

        WebClient.create(tokenRenewalApiUri)
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                .with("client_id", apiKey)
                .with("client_secret", apiSecret)
                .with("refresh_token", kakaoToken.getRefreshToken()))
            .retrieve()
            .bodyToMono(Map.class)
            .doOnNext(tokenResponse -> {
                log.info(tokenResponse.toString());
                String refreshToken = (String)Optional.ofNullable(tokenResponse.get("refresh_token"))
                    .orElseGet(kakaoToken::getRefreshToken);
                kakaoTokenOauthService.saveOrUpdateToken(tokenResponse.get("access_token").toString(),
                    refreshToken, kakaoToken.getMember());
            })
            .flatMap(tokenResponse -> {
                // 토큰 갱신 후 다시 메시지 보내기
                String renewedAccessToken = tokenResponse.get("access_token").toString();
                sendMessage(template, renewedAccessToken);
                return Mono.empty();
            })
            .subscribe();
    }

    @Async
    public void sendWelcomeMessage(Member member, String accessToken) {
        KakaoTemplate.Feed feedTemplate = kakaoTemplateConstructor.getWelcomeTemplate(member);

        sendMessage(feedTemplate, accessToken);
    }

    public void unlinkKaKaoService(String accessToken) {
        WebClient.create(unlinkKakaoApiUri)
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe();
    }
}
