package hankyu.board.spring_board.service.sign;

import hankyu.board.spring_board.config.jwt.TokenProvider;
import hankyu.board.spring_board.dto.sign.LogoutRequest;
import hankyu.board.spring_board.dto.sign.SignUpRequest;
import hankyu.board.spring_board.dto.token.TokenReissueRequest;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.event.sign.SignUpEvent;
import hankyu.board.spring_board.exception.member.DuplicateEmailException;
import hankyu.board.spring_board.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.exception.sign.InvalidRefreshTokenException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.service.redis.RedisKey;
import hankyu.board.spring_board.service.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hankyu.board.spring_board.factory.dto.sign.LogoutRequestFactory.createLogoutRequest;
import static hankyu.board.spring_board.factory.dto.sign.SignUpRequestFactory.createSignUpRequest;
import static hankyu.board.spring_board.factory.dto.sign.TokenReissueRequestFactory.createTokenReissueRequest;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignServiceTest {

    @InjectMocks
    SignService signService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    ApplicationEventPublisher publisher;
    @Mock
    RedisService redisService;

    @BeforeEach
    public void setup() {
        signService = new SignService(memberRepository, authenticationManagerBuilder, tokenProvider, passwordEncoder, publisher, redisService);
    }

    @Test
    void signUp_Success() {
        //given
        SignUpRequest req = createSignUpRequest();
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname(anyString())).thenReturn(false);
        doNothing().when(publisher).publishEvent(any(SignUpEvent.class));

        //when
        signService.signUp(req);

        //then
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(publisher, times(1)).publishEvent(any(SignUpEvent.class));
    }

    @Test
    void signUp_DuplicateNickname_ThrowsException() {
        //given
        SignUpRequest req = createSignUpRequest();
        when(memberRepository.existsByNickname(req.getNickname())).thenReturn(true);

        // then
        assertThrows(DuplicateNicknameException.class, () -> signService.signUp(req));
    }

    @Test
    void signUp_DuplicateEmail_ThrowsException() {
        //given
        SignUpRequest req = createSignUpRequest();
        when(memberRepository.existsByEmail(req.getEmail())).thenReturn(true);

        // then
        assertThrows(DuplicateEmailException.class, () -> signService.signUp(req));
    }

    @Test
    @Transactional
    void logout_Success() {
        // Given
        LogoutRequest req = createLogoutRequest();
        when(tokenProvider.validateToken(req.getRefreshToken())).thenReturn(true);
        when(tokenProvider.getAuthentication(anyString())).thenReturn(createAuthentication());
        when(tokenProvider.getExpiration(anyString())).thenReturn(3600L); // 1 hour

        // When
        signService.logout(req);

        // Then
        verify(redisService, times(1)).deleteData(RedisKey.REFRESH_TOKEN, req.getRefreshToken());
        verify(redisService, times(1)).setDataWithExpiration(eq(RedisKey.BLACK_LIST), anyString(), eq(req.getAccessToken()), eq(3600L));
    }


    @Test
    void logout_InvalidRefreshToken_ThrowsException() {
        // Given
        LogoutRequest req = createLogoutRequest();
        when(tokenProvider.validateToken(req.getRefreshToken())).thenReturn(false); // Simulate failure

        // When & Then
        assertThrows(InvalidRefreshTokenException.class, () -> signService.logout(req)); // Replace with the actual exception type
    }

    @Test
    void reissue_Success() {
        // Given
        TokenReissueRequest req = createTokenReissueRequest();
        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.getAuthentication(eq(req.getAccessToken()))).thenReturn(createAuthentication());
        when(redisService.getData(eq(RedisKey.REFRESH_TOKEN), anyString())).thenReturn(req.getRefreshToken());
        when(tokenProvider.generateAccessToken(createAuthentication())).thenReturn("accessToken");

        // When
        String newAccessToken = signService.reissue(req);

        // Then
        assertEquals(newAccessToken, "accessToken");
    }

    @Test
    void reissue_InvalidRefreshToken_ThrowsException() {
        // Given
        TokenReissueRequest req = createTokenReissueRequest();
        when(tokenProvider.validateToken(req.getRefreshToken())).thenReturn(false); // Simulate failure

        // When & Then
        assertThrows(InvalidRefreshTokenException.class, () -> signService.reissue(req)); // Replace with the actual exception type
    }

    @Test
    void deleteMemberByExpiredEmailAuth_Success() {
        // given
        List<Member> expiredMembers = IntStream.rangeClosed(0,10)
                .mapToObj(i -> createMember("test" + i + "@email.com","123456a!", "testUsername" + i , "testNickname" + i))
                .collect(Collectors.toList());  // 만료된 회원 리스트를 생성
        when(memberRepository.findMembersByCreatedAtBeforeAndEnabled(any(LocalDateTime.class), anyBoolean())).thenReturn(expiredMembers);

        // when
        signService.deleteMemberByExpiredEmailAuth();

        // then
        await().atMost(10, SECONDS) // 최대 10초까지 기다립니다. 필요에 따라 조절 가능.
                .until(() -> {
                    return memberRepository.count() == 0;
                });
    }

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken("testuser", "password");
    }
}