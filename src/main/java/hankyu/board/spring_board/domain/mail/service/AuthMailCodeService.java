package hankyu.board.spring_board.domain.mail.service;

import hankyu.board.spring_board.domain.mail.entity.AuthMailCode;
import hankyu.board.spring_board.domain.mail.repository.AuthMailCodeRepository;
import hankyu.board.spring_board.global.exception.mail.AuthMailCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthMailCodeService {
    private final AuthMailCodeRepository authMailCodeRepository;

    public void saveOrUpdateAuthCode(String authCode, String email) {
        Optional<AuthMailCode> authMailCode = authMailCodeRepository.findByEmail(email);
        authMailCode.ifPresent(authMailCodeRepository::delete);
        authMailCodeRepository.save(AuthMailCode.builder().authCode(authCode).email(email).build());
    }

    @Transactional(readOnly = true)
    public AuthMailCode getAuthMailCodeByEmail(String email) {
        return authMailCodeRepository.findByEmail(email).orElseThrow(AuthMailCodeNotFoundException::new);
    }

    public void removeAuthCode(String email) {
        authMailCodeRepository.findByEmail(email).ifPresent(authMailCodeRepository::delete);
    }
}
