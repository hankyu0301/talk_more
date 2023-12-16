package hankyu.board.spring_board.domain.post.repository;

import hankyu.board.spring_board.domain.post.dto.PostReadCondition;
import hankyu.board.spring_board.domain.post.dto.PostSimpleDto;
import org.springframework.data.domain.Page;

public interface CustomPostRepository {
    Page<PostSimpleDto> findAllByCondition(PostReadCondition cond);
}
