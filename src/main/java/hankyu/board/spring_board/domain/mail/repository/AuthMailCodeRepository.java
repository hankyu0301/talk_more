package hankyu.board.spring_board.domain.mail.repository;

import hankyu.board.spring_board.domain.mail.entity.AuthMailCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthMailCodeRepository extends CrudRepository<AuthMailCode, String> {
    Optional<AuthMailCode> findByEmail(String email);
}

