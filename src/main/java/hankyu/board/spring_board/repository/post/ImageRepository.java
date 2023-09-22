package hankyu.board.spring_board.repository.post;

import hankyu.board.spring_board.entity.post.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
