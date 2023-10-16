package hankyu.board.spring_board.factory.dto.comment;

import hankyu.board.spring_board.dto.comment.CommentReadCondition;

public class CommentReadConditionFactory {
    public static CommentReadCondition createCommentReadCondition() {
        return new CommentReadCondition(1L);
    }

    public static CommentReadCondition createCommentReadCondition(Long postId) {
        return new CommentReadCondition(postId);
    }
}