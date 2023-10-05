package hankyu.board.spring_board.service.member;

import hankyu.board.spring_board.config.jwt.TokenProvider;
import hankyu.board.spring_board.dto.member.MemberDeleteRequest;
import hankyu.board.spring_board.dto.member.MemberDto;
import hankyu.board.spring_board.dto.member.MemberUpdateRequest;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.service.redis.RedisKey;
import hankyu.board.spring_board.service.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.member.MemberDeleteRequestFactory.createMemberDeleteRequest;
import static hankyu.board.spring_board.factory.dto.member.MemberUpdateReuqestFactory.createMemberUpdateRequest;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks MemberService memberService;

    @Mock MemberRepository memberRepository;
    @Mock
    TokenProvider tokenProvider;

    @Mock
    RedisService redisService;

    @Test
    void findMember_Success() {
        //given
        Member member = createMember();
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        //when
        MemberDto memberDto = memberService.findMember(member.getId());

        //then
        assertNotNull(memberDto);
        assertEquals(member.getId(), memberDto.getId());
    }

    @Test
    void findMember_MemberNotFoundException_ThrowsException() {
        //given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.findMember(memberId));
    }

    @Test
    void deleteMember_ValidRequest_Success() {
        // given
        Member member = createMember();
        MemberDeleteRequest req = createMemberDeleteRequest();
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(tokenProvider.getAuthentication(anyString())).thenReturn(createAuthentication());
        when(tokenProvider.getExpiration(anyString())).thenReturn(3600L); // 1 hour
        doNothing().when(memberRepository).delete(member);

        // when
        memberService.delete(member.getId(), req);

        // then
        verify(memberRepository, times(1)).findById(member.getId());
        verify(memberRepository, times(1)).delete(member);
        verify(redisService, times(1)).setDataWithExpiration(eq(RedisKey.BLACK_LIST), anyString(), eq(req.getAccessToken()), eq(3600L));
    }

    @Test
    void delete_MemberNotFound_ThrowsException() {
        // given
        Long memberId = 1L;
        MemberDeleteRequest req = createMemberDeleteRequest();
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> memberService.delete(memberId, req));
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, never()).delete(any(Member.class));

    }

    @Test
    void update_ValidRequest_Success() {
        // given
        Long memberId = 1L;
        Member member = createMember();
        MemberUpdateRequest request = createMemberUpdateRequest();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname(request.getNickname())).thenReturn(false);

        // when
        memberService.update(memberId, request);

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void update_MemberNotFound_ThrowsException() {
        // given
        Long memberId = 1L;
        MemberUpdateRequest request = createMemberUpdateRequest();
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // then
        assertThrows(MemberNotFoundException.class, () -> memberService.update(memberId, request));
    }

    @Test
    void update_DuplicateNickname_ThrowsException() {
        // given
        Long memberId = 1L;
        Member member = createMember();
        MemberUpdateRequest request = createMemberUpdateRequest();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname(request.getNickname())).thenReturn(true);

        // then
        assertThrows(DuplicateNicknameException.class, () -> memberService.update(memberId, request));
    }

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken("testuser", "password");
    }
}
