package hankyu.board.spring_board.domain.oauth.service;

import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.oauth.entity.KakaoToken;
import hankyu.board.spring_board.domain.oauth.repository.KakaoTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class KakaoTokenOauthService {

    private final KakaoTokenRepository kakaoTokenRepository;

    public void saveOrUpdateToken(String accessToken, String refreshToken, Member member) {
        Optional<KakaoToken> findToken = kakaoTokenRepository.findByMember(member);
        if (findToken.isEmpty()) {
            KakaoToken token = KakaoToken.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
            kakaoTokenRepository.save(token);
        } else {
            findToken.get().updateToken(accessToken, refreshToken);
        }
    }

    public void saveTestToken(KakaoToken kakaoToken) {
        kakaoTokenRepository.save(kakaoToken);
    }
}
