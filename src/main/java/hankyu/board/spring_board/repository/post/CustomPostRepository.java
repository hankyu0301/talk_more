package hankyu.board.spring_board.repository.post;

import hankyu.board.spring_board.dto.post.PostReadCondition;
import hankyu.board.spring_board.dto.post.PostSimpleDto;
import org.springframework.data.domain.Page;

public interface CustomPostRepository {
    Page<PostSimpleDto> findAllByCondition(PostReadCondition cond);
}
