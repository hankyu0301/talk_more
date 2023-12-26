package hankyu.board.spring_board.domain.member.service;

import hankyu.board.spring_board.domain.member.dto.MemberCreateRequest;
import hankyu.board.spring_board.domain.member.dto.MemberDto;
import hankyu.board.spring_board.domain.member.dto.MemberUpdateRequest;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.entity.MemberRole;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.oauth.entity.KakaoToken;
import hankyu.board.spring_board.domain.oauth.repository.KakaoTokenRepository;
import hankyu.board.spring_board.domain.oauth.service.KakaoApiService;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.member.DuplicateEmailException;
import hankyu.board.spring_board.global.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthUtils authUtils;
    private final PasswordEncoder passwordEncoder;
    private final KakaoApiService kakaoApiService;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void create(MemberCreateRequest request) {
        validateSignUpRequest(request);
        Member member = createMemberFromRequest(request);
        //아직 비활성화 상태 (email 인증 필요)
        memberRepository.save(member);
        //EventListener가 이벤트 생성을 감지하면 이메일 발송한 뒤 redis에 저장
        member.publishCreatedEvent(publisher);
    }

    @Transactional(readOnly = true)
    public MemberDto findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return MemberDto.toDto(member);
    }

    @Transactional
    public void update(Long id, MemberUpdateRequest req) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        authUtils.authorityCheck(member.getId());
        validateMemberUpdateRequest(req);
        member.update(req);
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        authUtils.authorityCheck(member.getId());
        if (member.getMemberRole().equals(MemberRole.ROLE_SOCIAL)) {
            Optional<KakaoToken> kakaoToken = kakaoTokenRepository.findByMember(member);
            kakaoToken.ifPresent(token -> kakaoApiService.unlinkKaKaoService(token.getAccessToken()));
        }
        memberRepository.delete(member);
    }

    private void validateMemberUpdateRequest(MemberUpdateRequest req) {
        if(memberRepository.existsByNickname(req.getNickname())) {
            throw new DuplicateNicknameException(req.getNickname());
        }
    }

    private void validateSignUpRequest(MemberCreateRequest request) {
        if(memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        if(memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException(request.getNickname());
        }
    }

    private Member createMemberFromRequest(MemberCreateRequest request) {
        return new Member(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(),
                request.getNickname());
    }
}
