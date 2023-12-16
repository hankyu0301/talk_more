package hankyu.board.spring_board.factory.dto.post;

import hankyu.board.spring_board.domain.post.dto.PostReadCondition;

import java.util.List;

public class PostReadConditionFactory {
    public static PostReadCondition createPostReadCondition(Integer page, Integer size) {
        return new PostReadCondition(page, size, null, List.of(), null);
    }

    public static PostReadCondition createPostReadCondition(Integer page, Integer size,String keyword, List<Long> categoryIds, Long memberId) {
        return new PostReadCondition(page, size, keyword, categoryIds, memberId);
    }
}
