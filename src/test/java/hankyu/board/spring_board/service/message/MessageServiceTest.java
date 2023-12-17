package hankyu.board.spring_board.service.message;

import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.message.dto.*;
import hankyu.board.spring_board.domain.message.entity.Message;
import hankyu.board.spring_board.domain.message.repository.MessageRepository;
import hankyu.board.spring_board.domain.message.service.MessageService;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.message.MessageNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.message.MessageCreateRequestFactory.createMessageCreateRequest;
import static hankyu.board.spring_board.factory.dto.message.MessageDeleteRequestFactory.createMessageDeleteRequest;
import static hankyu.board.spring_board.factory.dto.message.MessageReadConditionFactory.createMessageReadCondition;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.message.MessageFactory.createMessage;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    MessageService messageService;

    @Mock
    MessageRepository messageRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    AuthUtils authUtils;

    @Test
    void readAllSentMessageByCond_Success() {
        //given
        MessageReadCondition cond = createMessageReadCondition();
        given(authUtils.getMemberId()).willReturn(1L);
        given(messageRepository.findAllBySenderIdOrderByMessageIdDesc(1L, cond.getTargetId(), cond.getKeyword(), PageRequest.of(cond.getPage(), cond.getSize()))).willReturn(Page.empty());

        //when
        MessageListDto messageListDto = messageService.readAllSentMessageByCond(cond);

        //then
        assertThat(messageListDto.getMessageList().size()).isZero();
    }

    @Test
    void readAllReceivedMessageByCond_Success() {
        //given
        MessageReadCondition cond = createMessageReadCondition();
        given(authUtils.getMemberId()).willReturn(1L);
        given(messageRepository.findAllByReceiverIdOrderByMessageIdDesc(1L, cond.getTargetId(), cond.getKeyword(), PageRequest.of(cond.getPage(), cond.getSize()))).willReturn(Page.empty());

        //when
        MessageListDto messageListDto = messageService.readAllReceivedMessageByCond(cond);

        //then
        assertThat(messageListDto.getMessageList().size()).isZero();
    }

    @Test
    void read_Success() {
        // given
        Long id = 1L;
        Message message = createMessage();
        given(messageRepository.findWithSenderAndReceiverById(id)).willReturn(Optional.of(message));

        // when
        MessageDto result = messageService.read(id);

        // then
        Assertions.assertThat(result.getContent()).isEqualTo(message.getContent());
    }

    @Test
    void read_messageNotFound_ThrowsException() {
        // given
        Long id = 1L;
        given(messageRepository.findWithSenderAndReceiverById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.read(id)).isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    void create_Success() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(authUtils.getMemberId())).willReturn(Optional.of(createMember()));
        given(memberRepository.findById(req.getReceiverId())).willReturn(Optional.of(createMember()));

        // when
        messageService.create(req);

        // then
        verify(messageRepository).save(any());
    }

    @Test
    void create_senderNotFoundException_ThrowsException() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(authUtils.getMemberId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> messageService.create(req)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void create_receiverNotFoundException_ThrowsException() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(authUtils.getMemberId())).willReturn(Optional.of(createMember()));
        given(memberRepository.findById(req.getReceiverId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> messageService.create(req)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void deleteBySenderNotDeletableTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        MessageDeleteRequest req = createMessageDeleteRequest(List.of(id));
        given(messageRepository.findByIdIn(List.of(id))).willReturn(List.of(message));

        // when
        messageService.deleteBySender(req);

        // then
        Assertions.assertThat(message.isDeletedBySender()).isTrue();
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteByReceiverNotDeletableTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        MessageDeleteRequest req = createMessageDeleteRequest(List.of(id));
        given(messageRepository.findByIdIn(List.of(id))).willReturn(List.of(message));

        // when
        messageService.deleteByReceiver(req);

        // then
        Assertions.assertThat(message.isDeletedByReceiver()).isTrue();
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteBySender_messageNotFound_ThrowsException() {
        // given
        Long id = 1L;
        MessageDeleteRequest req = createMessageDeleteRequest(List.of(id));
        given(messageRepository.findByIdIn(List.of(id))).willReturn(List.of());

        // when, then
        messageService.deleteBySender(req);

        //then
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteByReceiver_messageNotFound() {
        // given
        MessageDeleteRequest req = createMessageDeleteRequest(List.of(1L, 2L));
        given(messageRepository.findByIdIn(List.of(1L, 2L))).willReturn(List.of());

        // when
        messageService.deleteByReceiver(req);

        // then
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteBySender_messageNotFound() {
        // given
        MessageDeleteRequest req = createMessageDeleteRequest(List.of(1L, 2L));
        given(messageRepository.findByIdIn(List.of(1L, 2L))).willReturn(List.of());

        // when
        messageService.deleteBySender(req);

        // then
        verify(messageRepository, never()).delete(any(Message.class));
    }
}
