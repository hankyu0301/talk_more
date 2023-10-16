package hankyu.board.spring_board.service.message;

import hankyu.board.spring_board.auth.AuthChecker;
import hankyu.board.spring_board.dto.message.*;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.message.Message;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.exception.message.MessageNotFoundException;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final AuthChecker authChecker;

    /*  보낸 Message*/
    @Transactional(readOnly = true)
    public MessageListDto readAllSentMessageByCond(MessageReadCondition cond) {
        return MessageListDto.toDto(
                messageRepository.findAllBySenderIdOrderByMessageIdDesc(authChecker.getMemberId(), cond.getTargetId(), cond.getKeyword(),  PageRequest.of(cond.getPage(), cond.getSize()))
        );
    }

    /*  받은 Message*/
    @Transactional(readOnly = true)
    public MessageListDto readAllReceivedMessageByCond(MessageReadCondition cond) {
        return MessageListDto.toDto(
                messageRepository.findAllByReceiverIdOrderByMessageIdDesc(authChecker.getMemberId(),cond.getTargetId(), cond.getKeyword(),  PageRequest.of(cond.getPage(), cond.getSize()))
        );
    }

    @Transactional(readOnly = true)
    public MessageDto read(Long id) {
        return MessageDto.toDto(
                messageRepository.findWithSenderAndReceiverById(id).orElseThrow(MessageNotFoundException::new)
        );
    }

    /*  Message 보내기*/
    @Transactional
    public void create(MessageCreateRequest req) {
        Member sender = memberRepository.findById(authChecker.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Member receiver = memberRepository.findById(req.getReceiverId()).orElseThrow(MemberNotFoundException::new);
        Message message = new Message(req.getContent(), sender, receiver);
        messageRepository.save(message);
    }

    /*  보낸 Message 삭제*/
    @Transactional
    public void deleteBySender(MessageDeleteRequest req) {
        // 제거할 메세지를 In 으로 한번에 조회 -> 조회한 메세지가 내가 보낸 메세지가 맞는지 확인
        List<Message> deletedMessages = messageRepository.findByIdIn(req.getDeletedMessageIds());
        messageRepository.deleteAll(deleteBySender(deletedMessages, Message::deleteBySender));
    }

    /*  받은 Message 삭제*/
    @Transactional
    public void deleteByReceiver(MessageDeleteRequest req) {
        List<Message> deletedMessages = messageRepository.findByIdIn(req.getDeletedMessageIds());
        messageRepository.deleteAll(deleteByReceiver(deletedMessages, Message::deleteByReceiver));
    }

    private List<Message> deleteBySender(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
        return deletedMessages.stream()
                .peek(message -> authChecker.authorityCheck(message.getSender().getId()))
                .peek(deleteFunction)
                .filter(Message::isDeletable)
                .collect(toList());
    }

    private List<Message> deleteByReceiver(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
        return deletedMessages.stream()
                .peek(message -> authChecker.authorityCheck(message.getReceiver().getId()))
                .peek(deleteFunction)
                .filter(Message::isDeletable)
                .collect(toList());
    }

}