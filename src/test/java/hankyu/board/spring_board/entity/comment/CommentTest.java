package hankyu.board.spring_board.entity.comment;

import hankyu.board.spring_board.domain.comment.entity.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.entity.comment.CommentFactory.createComment;
import static hankyu.board.spring_board.factory.entity.comment.CommentFactory.createCommentWithContent;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentTest {

    @Test
    void comment_Delete() {
        // given
        Comment comment = createComment(null);
        boolean beforeDeleted = comment.isDeleted();

        // when
        comment.markAsDeleted();

        // then
        boolean afterDeleted = comment.isDeleted();
        assertThat(beforeDeleted).isFalse();
        assertThat(afterDeleted).isTrue();
    }

    @Test
    void findDeletableComment_Success() {
        // given

        // root 1
        // 1 -> 2
        // 2(del) -> 3(del)
        // 2(del) -> 4
        // 3(del) -> 5(del)
        // 5(del) -> 6(del)
        // 5(del) -> 7(del)
        Comment comment1 = createCommentWithContent("content1",null);
        Comment comment2 = createCommentWithContent("content2",comment1);
        Comment comment3 = createCommentWithContent("content3",comment1);
        Comment comment4 = createCommentWithContent("content4",comment3);
        Comment comment5 = createCommentWithContent("content5",comment3);
        Comment comment6 = createCommentWithContent("content6",comment5);
        Comment comment7 = createCommentWithContent("content7",comment5);
        Comment comment8 = createCommentWithContent("content8",comment7);
        Comment comment9 = createCommentWithContent("content9",comment7);

        ReflectionTestUtils.setField(comment1, "children", List.of(comment2, comment3));
        ReflectionTestUtils.setField(comment2, "children", List.of());
        ReflectionTestUtils.setField(comment3, "children", List.of(comment4,comment5));
        ReflectionTestUtils.setField(comment4, "children", List.of());
        ReflectionTestUtils.setField(comment5, "children", List.of(comment6,comment7));
        ReflectionTestUtils.setField(comment6, "children", List.of());
        ReflectionTestUtils.setField(comment7, "children", List.of(comment8,comment9));

        comment1.delete();
        comment2.delete();
        comment4.delete();
        comment5.delete();
        comment8.delete();
        comment9.delete();

        // when
        Optional<Comment> deletableComment = comment7.delete();

        // then
        assertThat(deletableComment).containsSame(comment7);
    }

}
