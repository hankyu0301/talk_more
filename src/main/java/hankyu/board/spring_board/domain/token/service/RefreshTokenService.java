package hankyu.board.spring_board.domain.token.service;

import hankyu.board.spring_board.domain.token.entity.RefreshToken;
import hankyu.board.spring_board.domain.token.repository.RefreshTokenRepository;
import hankyu.board.spring_board.global.exception.sign.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveTokenInfo(Long memberId, String refreshToken, String accessToken) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(memberId), refreshToken, accessToken));
    }

    public void removeRefreshToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
            .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional(readOnly = true)
    public RefreshToken getTokenByAccessToken(String accessToken) {
        return refreshTokenRepository.findByAccessToken(accessToken)
            .orElseThrow(RefreshTokenNotFoundException::new);
    }

}
