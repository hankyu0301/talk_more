package hankyu.board.spring_board.domain.message.service;

import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.message.dto.*;
import hankyu.board.spring_board.domain.message.entity.Message;
import hankyu.board.spring_board.domain.message.repository.MessageRepository;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.message.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final AuthUtils authUtils;

    /*  보낸 Message*/
    @Transactional(readOnly = true)
    public MessageListDto readAllSentMessageByCond(MessageReadCondition cond) {
        return MessageListDto.toDto(
                messageRepository.findAllBySenderIdOrderByMessageIdDesc(authUtils.getMemberId(), cond.getTargetId(), cond.getKeyword(),  PageRequest.of(cond.getPage(), cond.getSize()))
        );
    }

    /*  받은 Message*/
    @Transactional(readOnly = true)
    public MessageListDto readAllReceivedMessageByCond(MessageReadCondition cond) {
        return MessageListDto.toDto(
                messageRepository.findAllByReceiverIdOrderByMessageIdDesc(authUtils.getMemberId(),cond.getTargetId(), cond.getKeyword(),  PageRequest.of(cond.getPage(), cond.getSize()))
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
        Member sender = memberRepository.findById(authUtils.getMemberId()).orElseThrow(MemberNotFoundException::new);
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

    /*  1.  삭제하고자 하는 메세지를 한건씩 조회합니다.
    *   2.  삭제 권한 있는지 확인합니다.
    *   3.  메세지의 상태를 '삭제 되었음' 으로 변경합니다.
    *   4.  메세지를 실제로 삭제해도 되는지 (수신자, 발신자 모두 '삭제 되었음' 상태로 변경했는지) 확인합니다.
    *   5.  실제로 삭제해도 되는 메세지는 List에 담아 반환합니다.*/
    private List<Message> deleteBySender(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
        List<Message> result = new ArrayList<>();
        for (Message message : deletedMessages) { //    1
            authUtils.authorityCheck(message.getSender().getId());    //  2
            deleteFunction.accept(message); //  3
            if (message.isDeletable()) {    //  4
                result.add(message);    //  5
            }
        }
        return result;
    }

    private List<Message> deleteByReceiver(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
        List<Message> result = new ArrayList<>();
        for (Message message : deletedMessages) {
            authUtils.authorityCheck(message.getReceiver().getId());
            deleteFunction.accept(message);
            if (message.isDeletable()) {
                result.add(message);
            }
        }
        return result;
    }

}