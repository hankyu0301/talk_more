package hankyu.board.spring_board.service.comment;

import hankyu.board.spring_board.aop.AuthChecker;
import hankyu.board.spring_board.dto.comment.CommentCreateRequest;
import hankyu.board.spring_board.dto.comment.CommentDto;
import hankyu.board.spring_board.dto.comment.CommentReadCondition;
import hankyu.board.spring_board.entity.comment.Comment;
import hankyu.board.spring_board.exception.comment.CommentNotFoundException;
import hankyu.board.spring_board.exception.helper.CannotConvertHelperException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.exception.post.PostNotFoundException;
import hankyu.board.spring_board.repository.comment.CommentRepository;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static hankyu.board.spring_board.dto.comment.CommentDto.toDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AuthChecker authChecker;

    @Transactional(readOnly = true)
    public List<CommentDto> readAll(CommentReadCondition cond) {
        List<Comment> comments = commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId());
        return createCommentDtoList(comments);
    }

    @Transactional
    public void create(CommentCreateRequest req) {
        Comment comment = new Comment(
                req.getContent(),
                memberRepository.findById(authChecker.getMemberId()).orElseThrow(MemberNotFoundException::new),
                postRepository.findById(req.getPostId()).orElseThrow(PostNotFoundException::new),
                Optional.ofNullable(req.getParentId())
                        .map(id -> commentRepository.findById(id).orElseThrow(CommentNotFoundException::new))
                        .orElse(null));
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findWithMemberById(id).orElseThrow(CommentNotFoundException::new);
        authChecker.authorityCheck(comment.getMember());
        /*  commentRepository.delete()를 사용하여 삭제가능한 최상위 댓글을 삭제 ->CASCADE 설정으로 하위댓글이 일괄삭제됨.*/
        /*  삭제 가능한 댓글이 없어서 Optional.empty()를 반환받는다면 comment::delete로 comment.deleted = true; 로 변경만함.*/
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }

    private List<CommentDto> createCommentDtoList(List<Comment> comments) {
        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> roots = new ArrayList<>();
        for (Comment comment : comments) {
            Long id = comment.getId();
            CommentDto dto = toDto(comment);
            commentMap.put(id, dto);
            Long parentId = comment.getParent().getId();
            if(parentId == null) {
                roots.add(dto);
            } else {
                try {
                    CommentDto parentDto = commentMap.get(parentId);
                    parentDto.getChildren().add(dto);
                } catch (NullPointerException e) {
                    throw new CannotConvertHelperException(e.getMessage());
                }
            }
        }
        return roots;
    }
}