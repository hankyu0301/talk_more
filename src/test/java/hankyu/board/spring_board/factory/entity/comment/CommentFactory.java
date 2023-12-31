package hankyu.board.spring_board.factory.entity.comment;


import hankyu.board.spring_board.domain.comment.entity.Comment;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.post.entity.Post;

import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPost;

public class CommentFactory {

    public static Comment createComment(Comment parent) {
        return new Comment("content", createMember(), createPost(), parent);
    }

    public static Comment createCommentWithContent(String content, Comment parent) {
        Comment comment = new Comment(content, createMember(), createPost(), parent);
        return comment;
    }

    public static Comment createDeletedComment(Comment parent) {
        Comment comment = new Comment("content", createMember(), createPost(), parent);
        comment.markAsDeleted();
        return comment;
    }

    public static Comment createComment(Member member, Post post, Comment parent) {
        return new Comment("content", member, post, parent);
    }
}