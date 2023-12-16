package hankyu.board.spring_board.domain.token.repository;


import hankyu.board.spring_board.domain.token.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
