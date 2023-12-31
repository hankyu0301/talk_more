package hankyu.board.spring_board.domain.member.repository;

import hankyu.board.spring_board.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    List<Member> findMembersByCreatedAtBeforeAndEnabled(LocalDateTime now, Boolean enabled);
}
