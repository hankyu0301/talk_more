package hankyu.board.spring_board.domain.oauth.repository;


import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.oauth.entity.KakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {
    Optional<KakaoToken> findByAccessToken(String accessToken);

    Optional<KakaoToken> findByMember(Member member);
}
