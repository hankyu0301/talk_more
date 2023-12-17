package hankyu.board.spring_board.domain.post.repository;

import hankyu.board.spring_board.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    @Query("select p from Post p join fetch p.member where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);
}
