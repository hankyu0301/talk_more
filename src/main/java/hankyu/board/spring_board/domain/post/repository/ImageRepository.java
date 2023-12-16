package hankyu.board.spring_board.domain.post.repository;

import hankyu.board.spring_board.domain.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
