package hankyu.board.spring_board.service.comment;

import hankyu.board.spring_board.domain.comment.dto.CommentCreateRequest;
import hankyu.board.spring_board.domain.comment.dto.CommentDto;
import hankyu.board.spring_board.domain.comment.dto.CommentReadCondition;
import hankyu.board.spring_board.domain.comment.entity.Comment;
import hankyu.board.spring_board.domain.comment.repository.CommentRepository;
import hankyu.board.spring_board.domain.comment.service.CommentService;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.post.repository.PostRepository;
import hankyu.board.spring_board.global.auth.AuthChecker;
import hankyu.board.spring_board.global.exception.comment.CommentNotFoundException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.post.PostNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.comment.CommentCreateRequestFactory.createCommentCreateRequest;
import static hankyu.board.spring_board.factory.dto.comment.CommentCreateRequestFactory.createCommentCreateRequestWithParentId;
import static hankyu.board.spring_board.factory.dto.comment.CommentReadConditionFactory.createCommentReadCondition;
import static hankyu.board.spring_board.factory.entity.comment.CommentFactory.createComment;
import static hankyu.board.spring_board.factory.entity.comment.CommentFactory.createDeletedComment;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPost;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;
    @Mock CommentRepository commentRepository;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock AuthChecker authChecker;

    @Test
    void create_Success() {
        //given
        CommentCreateRequest req = createCommentCreateRequest();
        given(authChecker.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(req.getPostId())).willReturn(Optional.of(createPost()));

        //when
        commentService.create(req);

        //then
        verify(commentRepository).save(any());
    }

    @Test
    void create_memberNotFound_ThrowsException() {
        //given
        CommentCreateRequest req = createCommentCreateRequest();
        given(authChecker.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy( () -> commentService.create(req)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void create_postNotFound_ThrowsException() {
        //given
        CommentCreateRequest req = createCommentCreateRequest();
        given(authChecker.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(req.getPostId())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy( () -> commentService.create(req)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void create_parentCommentNotFound_ThrowsException() {
        //given
        CommentCreateRequest req = createCommentCreateRequestWithParentId(1L);
        given(authChecker.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(req.getPostId())).willReturn(Optional.of(createPost()));
        given(commentRepository.findById(req.getParentId())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy( () -> commentService.create(req)).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void readAll_Success() {
        //given
        CommentReadCondition cond = createCommentReadCondition();
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())).willReturn(List.of(createComment(null)));

        //when
        List<CommentDto> commentDtos = commentService.readAll(cond);

        // then
        assertThat(commentDtos.size()).isEqualTo(1);
    }

    @Test
    void readAllDeletedComment_Success() {
        //given
        CommentReadCondition cond = createCommentReadCondition();
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())).willReturn(List.of(createDeletedComment(null)));

        //when
        List<CommentDto> commentDtos = commentService.readAll(cond);

        // then
        assertThat(commentDtos.size()).isEqualTo(1);
        assertThat(commentDtos.get(0).getContent()).isNull();
    }

    @Test
    void delete_Success() {
        //given
        Comment comment = createComment(null);
        given(commentRepository.findWithMemberById(anyLong())).willReturn(Optional.of(comment));

        //when
        commentService.delete(1L);

        //then
        verify(commentRepository).findWithMemberById(any());
        verify(commentRepository).delete(any());
    }

    @Test
    void delete_commentNotFound_ThrowsException() {
        //given
        given(commentRepository.findWithMemberById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy( () -> commentService.delete(anyLong()))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
