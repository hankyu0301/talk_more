package hankyu.board.spring_board.domain.message.repository;

import hankyu.board.spring_board.domain.message.dto.MessageSimpleDto;
import hankyu.board.spring_board.domain.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m left join fetch m.sender left join fetch m.receiver where m.id = :id")
    Optional<Message> findWithSenderAndReceiverById(Long id);

    //  보낸 메세지 리스트
    @Query("select new hankyu.board.spring_board.dto.message.MessageSimpleDto(m.id, m.content, m.receiver.nickname, m.createdAt) " +
            "from Message m left join m.receiver " +
            "where m.sender.id = :senderId and (:targetId IS NULL OR m.receiver.id = :targetId) and (:keyword IS NULL OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) and m.deletedBySender = false order by m.id desc")
    Page<MessageSimpleDto> findAllBySenderIdOrderByMessageIdDesc(Long senderId, Long targetId, String keyword, Pageable pageable);

    //  받은 메세지 리스트
    @Query("select new hankyu.board.spring_board.dto.message.MessageSimpleDto(m.id, m.content, m.sender.nickname, m.createdAt) " +
            "from Message m left join m.sender " +
            "where m.receiver.id = :receiverId and (:targetId IS NULL OR m.sender.id = :targetId) and(:keyword IS NULL OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) and m.deletedByReceiver = false order by m.id desc")
    Page<MessageSimpleDto> findAllByReceiverIdOrderByMessageIdDesc(Long receiverId, Long targetId, String keyword, Pageable pageable);

   List<Message> findByIdIn(List<Long> deletedMessageIds);
}