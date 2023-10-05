package hankyu.board.spring_board.service.sign;

import hankyu.board.spring_board.config.jwt.TokenProvider;
import hankyu.board.spring_board.dto.sign.LogoutRequest;
import hankyu.board.spring_board.dto.sign.SignInRequest;
import hankyu.board.spring_board.dto.sign.SignUpRequest;
import hankyu.board.spring_board.dto.token.TokenReissueRequest;
import hankyu.board.spring_board.dto.token.TokenResponse;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.exception.member.DuplicateEmailException;
import hankyu.board.spring_board.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.exception.sign.InvalidRefreshTokenException;
import hankyu.board.spring_board.exception.sign.LoginFailureException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.service.redis.RedisKey;
import hankyu.board.spring_board.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignService {
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;
    private final RedisService redisService;

    /*  회원가입에 필요한 정보를 입력받고
    *   올바른 정보라면 가입처리(repository.save())한 뒤
    *   인증코드가 담긴 메일을 발송하는 Event 발행
    * */
    @Transactional
    public void signUp(SignUpRequest request) {
        validateSignUpRequest(request);
        Member member = createMemberFromRequest(request);
        //아직 비활성화 상태 (email 인증 필요)
        memberRepository.save(member);
        //EventListener가 이벤트 생성을 감지하면 이메일 발송한 뒤 redis에 저장
        member.publishCreatedEvent(publisher);
    }

    /*  로그인에 필요한 정보를 입력받고
    *   올바른 정보라면 access/refresh token을 생성하고 반환
    *   생성된 refreshToken은 token 유효시간동안 redis에 저장됨.
    * */
    @Transactional
    public TokenResponse signIn(SignInRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req, member);
        Authentication authentication = getUserAuthentication(req);
        TokenResponse res = tokenProvider.generateToken(authentication);
        redisService.setDataWithExpiration(RedisKey.REFRESH_TOKEN, res.getRefreshToken(), req.getEmail(), REFRESH_TOKEN_EXPIRE_TIME);
        return res;
    }

    /*  access/refresh Token을 전달받고
    *   refreshToken이 유효하다면 메서드 호출자의 email로 redis를 조회하여 refreshToken을 가져옴.
    *   전달받은 refreshToken과 redis의 refreshToken이 일치하다면 accessToken을 재발급
    * */
    @Transactional
    public String reissue(TokenReissueRequest req) {
        validateTokenReissueRequest(req.getRefreshToken());
        Authentication authentication = tokenProvider.getAuthentication(req.getAccessToken());
        String refreshToken = redisService.getData(RedisKey.REFRESH_TOKEN, req.getRefreshToken());
        validateRefreshTokenForReissue(refreshToken, req.getRefreshToken());
        return tokenProvider.generateAccessToken(authentication);
    }

    /*  access/refresh Token을 전달받고
    *   refreshToken은 제거하고 accessToken은 block 시킴
    * */
    @Transactional
    public void logout(LogoutRequest req) {
        validateRefreshToken(req.getRefreshToken());
        //  refreshToken 삭제하여 accessToken을 재발급하지 못하게 함.
        redisService.deleteData(RedisKey.REFRESH_TOKEN, req.getRefreshToken());

        //  이전에 발급받은 accessToken을 사용하지 못하도록 해야함.
        //  req.getAccessToken()으로 남은 유효시간을 읽어와서 유효시간동안 redis에 등록.
        //  redis에 등록된 accessToken으로 로그인이 불가(jwtAuthenticationFilter에서 확인)
        expireAccessToken(req.getAccessToken());
    }

    /*  이메일 미인증 상태이며 생성한지 24시간이 지난 계정들을 삭제
    *   이 메서드는 매일 자정에 실행*/
    @Transactional
    @Async("ThreadPoolTaskScheduler")
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteMemberByExpiredEmailAuth() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Member> expiredMembers = memberRepository.findMembersByCreatedAtBeforeAndEnabled(yesterday, false);
        memberRepository.deleteAll(expiredMembers);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
    }

    private void validateSignUpRequest(SignUpRequest request) {
        if(memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        if(memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException(request.getNickname());
        }
    }

    private Member createMemberFromRequest(SignUpRequest request) {
        return new Member(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(),
                request.getNickname());
    }

    private void validatePassword(SignInRequest signInRequest, Member member) {
        if (!passwordEncoder.matches(signInRequest.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }

    private void validateTokenReissueRequest(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
    }

    private void validateRefreshTokenForReissue(String refreshTokenFromDB, String refreshTokenFromRequest) {
        if (!StringUtils.hasText(refreshTokenFromDB) && !refreshTokenFromDB.equals(refreshTokenFromRequest))
            throw new InvalidRefreshTokenException(); //  만료되었거나, 로그아웃된, 일치하지 않는 refreshToken
    }


    private Authentication getUserAuthentication(SignInRequest req) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(token);
    }

    private void expireAccessToken(String accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        long expiration = tokenProvider.getExpiration(accessToken);
        redisService.setDataWithExpiration(RedisKey.BLACK_LIST, authentication.getName(), accessToken, expiration);
    }

}
