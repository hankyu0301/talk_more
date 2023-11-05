package hankyu.board.spring_board.entity.comment;

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
        Comment comment3 = createCommentWithContent("content3",comment2);
        Comment comment4 = createCommentWithContent("content4",comment2);
        Comment comment5 = createCommentWithContent("content5",comment3);
        Comment comment6 = createCommentWithContent("content6",comment5);
        Comment comment7 = createCommentWithContent("content7",comment5);

        comment2.markAsDeleted();
        comment3.markAsDeleted();
        comment5.markAsDeleted();
        comment6.markAsDeleted();
        comment7.markAsDeleted();

        ReflectionTestUtils.setField(comment1, "children", List.of(comment2));
        ReflectionTestUtils.setField(comment2, "children", List.of(comment3, comment4));
        ReflectionTestUtils.setField(comment3, "children", List.of(comment5));
        ReflectionTestUtils.setField(comment4, "children", List.of());
        ReflectionTestUtils.setField(comment5, "children", List.of(comment6,comment7));
        ReflectionTestUtils.setField(comment6, "children", List.of());
        ReflectionTestUtils.setField(comment7, "children", List.of());

        // when
        Optional<Comment> deletableComment = comment7.delete();

        // then
        assertThat(deletableComment).containsSame(comment3);
    }

    @Test
    void findDeletableComment_Success2() {
        // given

        // root 1
        // 1 -> 2
        // 2(del) -> 3(del)
        // 2(del) -> 4
        // 3(del) -> 5
        Comment comment1 = createComment(null);
        Comment comment2 = createComment(comment1);
        Comment comment3 = createComment(comment2);
        Comment comment4 = createComment(comment2);
        Comment comment5 = createComment(comment3);

        comment2.markAsDeleted();
        comment3.markAsDeleted();
        comment5.markAsDeleted();

        ReflectionTestUtils.setField(comment1, "children", List.of(comment2));
        ReflectionTestUtils.setField(comment2, "children", List.of(comment3, comment4));
        ReflectionTestUtils.setField(comment3, "children", List.of(comment5));
        ReflectionTestUtils.setField(comment4, "children", List.of());
        ReflectionTestUtils.setField(comment5, "children", List.of());

        // when
        Optional<Comment> deletableComment = comment5.delete();

        // then
        assertThat(deletableComment).containsSame(comment3);

    }
}
