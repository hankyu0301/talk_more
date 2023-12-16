package hankyu.board.spring_board.service.email;

import hankyu.board.spring_board.domain.mail.dto.EmailConfirmRequest;
import hankyu.board.spring_board.domain.mail.dto.ResendEmailRequest;
import hankyu.board.spring_board.domain.mail.entity.AuthMailCode;
import hankyu.board.spring_board.domain.mail.service.AuthMailCodeService;
import hankyu.board.spring_board.domain.mail.service.EmailService;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.global.exception.mail.EmailAlreadyVerifiedException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.email.EmailAuthRequestFactory.createEmailAuthRequest;
import static hankyu.board.spring_board.factory.dto.email.ResendEmailRequestFactory.createResendEmailRequest;
import static hankyu.board.spring_board.factory.dto.email.ResendEmailRequestFactory.createResendEmailRequestWithEmail;
import static hankyu.board.spring_board.factory.entity.mail.AuthMailCodeFactory.createAuthMailCode;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    EmailService emailService;
    @Mock
    AuthMailCodeService authMailCodeService;
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
        AuthMailCode authMailCode = createAuthMailCode();
        Member member = createMember();

        when(authMailCodeService.getAuthMailCodeByEmail(req.getEmail())).thenReturn(authMailCode);
        when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(member));

        // When
        emailService.confirmEmail(req);

        // Then
        verify(authMailCodeService, times(1)).getAuthMailCodeByEmail(req.getEmail());
        verify(memberRepository, times(1)).findByEmail(req.getEmail());
        verify(authMailCodeService, times(1)).removeAuthCode(req.getEmail());
    }


    @Test
    void confirmEmail_MemberNotFound_ThrowsException() {
        // Given
        EmailConfirmRequest req = createEmailAuthRequest();
        AuthMailCode authMailCode = createAuthMailCode();
        Member member = createMember();

        when(authMailCodeService.getAuthMailCodeByEmail(req.getEmail())).thenReturn(authMailCode);
        when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(MemberNotFoundException.class, () -> emailService.confirmEmail(req));

        verify(authMailCodeService, times(1)).getAuthMailCodeByEmail(req.getEmail());
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
    }

}
