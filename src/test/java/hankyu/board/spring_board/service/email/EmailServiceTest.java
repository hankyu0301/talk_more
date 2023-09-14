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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.member.EmailAuthRequestFactory.createEmailAuthRequest;
import static hankyu.board.spring_board.factory.dto.member.EmailAuthRequestFactory.createEmailAuthRequestWithInvalidCode;
import static hankyu.board.spring_board.factory.dto.member.ResendEmailRequestFactory.createResendEmailRequest;
import static hankyu.board.spring_board.factory.dto.member.ResendEmailRequestFactory.createResendEmailRequestWithEmail;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    EmailService emailService;
    @Mock RedisService redisService;
    @Mock MemberRepository memberRepository;
    @Mock JavaMailSender javaMailSender;

    @Test
    void sendEmail_Success() {
        // Given
        String email = "test@example.com";

        // When
        emailService.sendEmail(email);

        // Then
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void confirmEmail_Success() {
        // Given
        EmailConfirmRequest req = createEmailAuthRequest();
        Member member = createMember();

        when(redisService.getData(RedisKey.EMAIL, req.getCode())).thenReturn(req.getEmail());
        when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(member));

        // When
        emailService.confirmEmail(req);

        // Then
        verify(redisService, times(1)).getData(RedisKey.EMAIL, req.getCode());
        verify(memberRepository, times(1)).findByEmail(req.getEmail());
        verify(redisService, times(1)).deleteData(RedisKey.EMAIL, req.getCode());
    }

    @Test
    void confirmEmail_InvalidVerificationCode_ThrowsException() {
        // Given
        EmailConfirmRequest req = createEmailAuthRequestWithInvalidCode();
        when(redisService.getData(RedisKey.EMAIL, req.getCode())).thenReturn(null);

        // When / Then
        assertThrows(InvalidVerificationCodeException.class, () -> emailService.confirmEmail(req));

        verify(redisService, times(1)).getData(RedisKey.EMAIL, req.getCode());
        verifyNoInteractions(memberRepository);
    }

    @Test
    void confirmEmail_MemberNotFound_ThrowsException() {
        // Given
        EmailConfirmRequest req = createEmailAuthRequest();
        when(redisService.getData(RedisKey.EMAIL, req.getCode())).thenReturn(req.getEmail());
        when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(MemberNotFoundException.class, () -> emailService.confirmEmail(req));

        verify(redisService, times(1)).getData(RedisKey.EMAIL, req.getCode());
        verify(memberRepository, times(1)).findByEmail(req.getEmail());
    }
    @Test
    void resend_Success() {
        // Given
        Member member = createMember();
        ResendEmailRequest req = createResendEmailRequest();

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // When
        emailService.resend(req);

        // Then
        verify(memberRepository, times(1)).findByEmail(member.getEmail());
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void resend_AlreadyEmailVerified_ThrowsException() {
        // Given
        Member member = createMember();
        member.confirmEmail();
        ResendEmailRequest req = createResendEmailRequestWithEmail(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // When / Then
        assertThrows(EmailAlreadyVerifiedException.class, () -> emailService.resend(req));

        verify(memberRepository, times(1)).findByEmail(member.getEmail());
        verifyNoInteractions(redisService, javaMailSender);
    }

    @Test
    void resend_MemberNotFound_ThrowsException() {
        // Given
        Member member = createMember();
        ResendEmailRequest req = createResendEmailRequestWithEmail(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(MemberNotFoundException.class, () -> emailService.resend(req));

        verify(memberRepository, times(1)).findByEmail(member.getEmail());
        verifyNoInteractions(redisService, javaMailSender);
    }

}
