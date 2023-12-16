package hankyu.board.spring_board.domain.mail.service;

import hankyu.board.spring_board.domain.mail.dto.EmailConfirmRequest;
import hankyu.board.spring_board.domain.mail.dto.ResendEmailRequest;
import hankyu.board.spring_board.domain.mail.entity.AuthMailCode;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.global.exception.mail.AuthMailCodeMisMatchException;
import hankyu.board.spring_board.global.exception.mail.EmailAlreadyVerifiedException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final AuthMailCodeService authMailCodeService;

    @Value("${elastic.ip.address}")
    private String address;

    @Transactional
    public void sendEmail(String email) {
        String authCode = createCode();
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setTo(email);
        smm.setFrom("finebears@naver.com");
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("http://" + address + ":8080/api/confirm-email?email="+email+"&code="+authCode);
        authMailCodeService.saveOrUpdateAuthCode(authCode, email);
        javaMailSender.send(smm);
    }


    @Transactional
    public void confirmEmail(EmailConfirmRequest request) {
        //  EventListener가 redis에 저장한 <code, email> 조회
        AuthMailCode findAuthCode = authMailCodeService.getAuthMailCodeByEmail(request.getEmail());
        validateEmailAuthRequest(request, findAuthCode.getAuthCode());
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(MemberNotFoundException::new);
        member.confirmEmail();
        authMailCodeService.removeAuthCode(request.getEmail());
    }

    @Transactional
    public void resend(ResendEmailRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(MemberNotFoundException::new);
        isEmailAlreadyVerified(member);
        sendEmail(req.getEmail());
    }

    public String createCode() {
        Random random = new Random();

        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);
            switch (index) {
                case 0:
                    key.append((char)(random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char)(random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    //  redis에서 가져온 email과 사용자가 제출한 email이 일치하는지 검증
    private void validateEmailAuthRequest(EmailConfirmRequest request, String authCode) {
        if (!StringUtils.hasText(authCode) && !request.getCode().equals(authCode)) {
            throw new AuthMailCodeMisMatchException();
        }
    }

    //  이미 인증처리된 member라면 exception
    private void isEmailAlreadyVerified(Member member) {
        if(member.isEnabled()) {
            throw new EmailAlreadyVerifiedException(member.getEmail());
        }
    }

}
