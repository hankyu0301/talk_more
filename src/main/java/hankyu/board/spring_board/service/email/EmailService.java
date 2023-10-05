package hankyu.board.spring_board.service.email;

import hankyu.board.spring_board.dto.email.EmailConfirmRequest;
import hankyu.board.spring_board.dto.email.ResendEmailRequest;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.exception.email.EmailAlreadyVerifiedException;
import hankyu.board.spring_board.exception.email.InvalidVerificationCodeException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.service.redis.RedisKey;
import hankyu.board.spring_board.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private static final Long EMAIL_AUTH_EXPIRATION = 60*5L;

    @Transactional
    public void confirmEmail(EmailConfirmRequest request) {
        //  EventListener가 redis에 저장한 <code, email> 조회
        String email = redisService.getData(RedisKey.EMAIL, request.getCode());
        //  redis에서 가져온 email과 사용자가 제출한 email이 일치하는지 검증
        validateEmailAuthRequest(request, email);
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        member.confirmEmail();
        redisService.deleteData(RedisKey.EMAIL, request.getCode());
    }

    @Transactional
    public void resend(ResendEmailRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(MemberNotFoundException::new);
        isEmailAlreadyVerified(member);
        String verificationCode = sendEmail(req.getEmail());
        redisService.setDataWithExpiration(RedisKey.EMAIL, verificationCode, req.getEmail(), EMAIL_AUTH_EXPIRATION);
    }

    @Transactional
    public String sendEmail(String email) {
        String code = generateCode();
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setTo(email);
        smm.setFrom("finebears@naver.com");
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("http://localhost:8080/api/confirm-email?email="+email+"&code="+code);

        javaMailSender.send(smm);
        return code;
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

    //  redis에서 가져온 email과 사용자가 제출한 email이 일치하는지 검증
    private void validateEmailAuthRequest(EmailConfirmRequest request, String email) {
        if (!StringUtils.hasText(email) && !request.getEmail().equals(email)) {
            throw new InvalidVerificationCodeException();
        }
    }

    //  이미 인증처리된 member라면 exception
    private void isEmailAlreadyVerified(Member member) {
        if(member.isEnabled()) {
            throw new EmailAlreadyVerifiedException(member.getEmail());
        }
    }

}
