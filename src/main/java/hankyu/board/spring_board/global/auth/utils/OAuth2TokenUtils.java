package hankyu.board.spring_board.global.auth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2TokenUtils {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2AuthorizedClient getOAuth2AuthorizedClient(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            oauthToken.getAuthorizedClientRegistrationId(),
            oauthToken.getName()
        );
        return authorizedClient;
    }

    public String getOAuthAccessToken(OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getAccessToken().getTokenValue();
    }

    public String getOAuthRefreshToken(OAuth2AuthorizedClient authorizedClient) {
        if (authorizedClient.getRefreshToken() != null)
            return authorizedClient.getRefreshToken().getTokenValue();
        return null;
    }

    public String getOAuthRegistration(OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getClientRegistration().getRegistrationId();
    }
}
