package hankyu.board.spring_board.service.comment;

import hankyu.board.spring_board.dto.comment.CommentCreateRequest;
import hankyu.board.spring_board.dto.comment.CommentDto;
import hankyu.board.spring_board.dto.comment.CommentReadCondition;
import hankyu.board.spring_board.entity.comment.Comment;
import hankyu.board.spring_board.exception.comment.CommentNotFoundException;
import hankyu.board.spring_board.repository.comment.CommentRepository;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public List<CommentDto> readAll(CommentReadCondition cond) {
        return CommentDto.toDtoList(
                commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())
        );
    }

    @Transactional
    public void create(CommentCreateRequest req) {
        Comment comment = CommentCreateRequest.toEntity(req, memberRepository, postRepository, commentRepository);
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }
}