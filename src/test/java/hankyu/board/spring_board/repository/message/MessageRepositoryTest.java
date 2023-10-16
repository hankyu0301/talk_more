package hankyu.board.spring_board.repository.message;

import hankyu.board.spring_board.config.JPAConfig;
import hankyu.board.spring_board.config.QuerydslConfig;
import hankyu.board.spring_board.dto.message.MessageListDto;
import hankyu.board.spring_board.dto.message.MessageReadCondition;
import hankyu.board.spring_board.dto.message.MessageSimpleDto;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.message.Message;
import hankyu.board.spring_board.repository.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.IntStream;

import static hankyu.board.spring_board.factory.dto.message.MessageReadConditionFactory.createMessageReadCondition;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.message.MessageFactory.createMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, JPAConfig.class})
class MessageRepositoryTest {

    @Autowired MessageRepository messageRepository;

    @Autowired MemberRepository memberRepository;

    @PersistenceContext EntityManager em;

    Member member1, member2, member3;

    @BeforeEach
    void beforeEach() {
        member1 = memberRepository.save(createMember("finebears1@naver.com", "123456a!", "장한규1","finebears1" ));
        member2 = memberRepository.save(createMember("finebears2@naver.com", "123456a!", "장한규2","finebears2"));
        member3 = memberRepository.save(createMember("finebears3@naver.com", "123456a!", "장한규3","finebears3"));
        IntStream.rangeClosed(0, 100)
                .forEach(i -> messageRepository.save(createMessage("content" + i, member1, member2)));
        IntStream.rangeClosed(0, 100)
                .forEach(i -> messageRepository.save(createMessage("content" + i, member1, member3)));
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void findAllBySenderIdOrderByMessageIdDesc() {
        String keyword = "content";
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllBySenderIdOrderByMessageIdDesc(senderId, receiverId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDesc() {
        String keyword = "content";
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiverId, senderId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllBySenderIdOrderByMessageIdDesc_withNullKeyword() {
        String keyword = null;
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllBySenderIdOrderByMessageIdDesc(senderId, receiverId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDesc_withNullKeyword() {
        String keyword = null;
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiverId, senderId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }


    @Test
    void findAllBySenderIdOrderByMessageIdDesc_withInvalidKeyword() {
        String keyword = "invalidKeyword";
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllBySenderIdOrderByMessageIdDesc(senderId, receiverId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(0);
        assertThat(messageListDto.getTotalElements()).isEqualTo(0);
        assertThat(messageListDto.isHasNext()).isFalse();
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDesc_withInvalidKeyword() {
        String keyword = "invalidKeyword";
        Long senderId = member1.getId();
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiverId, senderId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(0);
        assertThat(messageListDto.getTotalElements()).isEqualTo(0);
        assertThat(messageListDto.isHasNext()).isFalse();
    }


    @Test
    void findAllBySenderIdOrderByMessageIdDesc_withNullTargetId() {
        String keyword = "content";
        Long senderId = member1.getId();
        Long receiverId = null;
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllBySenderIdOrderByMessageIdDesc(senderId, receiverId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(21);
        assertThat(messageListDto.getTotalElements()).isEqualTo(202);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDesc_withNullTargetId() {
        String keyword = "content";
        Long senderId = null;
        Long receiverId = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiverId, senderId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllBySenderIdOrderByMessageIdDesc_withDifferentTargetId() {
        String keyword = "content";
        Long senderId = member1.getId();
        Long receiverId2 = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllBySenderIdOrderByMessageIdDesc(senderId, receiverId2, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDesc_withDifferentTargetId() {
        String keyword = "content";
        Long senderId = null;
        Long receiverId1 = member2.getId();
        MessageReadCondition cond = createMessageReadCondition();

        Page<MessageSimpleDto> dtos = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiverId1, senderId, keyword, PageRequest.of(cond.getPage(), cond.getSize()));
        MessageListDto messageListDto = MessageListDto.toDto(dtos);

        assertThat(messageListDto.getTotalPages()).isEqualTo(11);
        assertThat(messageListDto.getTotalElements()).isEqualTo(101);
        assertThat(messageListDto.isHasNext()).isTrue();
    }

    @Test
    void findByIdIn_Success() {
        List<Long> deletedMessageIds = List.of(199L, 200L, 201L, 202L, 203L);
        List<Message> all = messageRepository.findAll();
        List<Message> foundMessageList = messageRepository.findByIdIn(deletedMessageIds);

        assertThat(foundMessageList.size()).isNotZero();
        assertThat(foundMessageList.size()).isEqualTo(4);
    }

    @Test
    void findByIdIn_withInvalidIds() {
        List<Long> deletedMessageIds = List.of(1199L, 1200L, 1201L, 1202L, 1203L);
        List<Message> foundMessageList = messageRepository.findByIdIn(deletedMessageIds);

        assertThat(foundMessageList.size()).isZero();
    }

    void clear() {
        em.flush();
        em.clear();
    }
}