package hankyu.board.spring_board.repository.post;

import hankyu.board.spring_board.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, CustomPostRepository {

    @Query("select p from Post p left join fetch p.member left join fetch p.images where p.id = :id")
    Optional<Post> findByIdWithMemberAndImages(Long id);

    Optional<Post> findById(Long id);

}
