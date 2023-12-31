package hankyu.board.spring_board.domain.comment.service;

import hankyu.board.spring_board.domain.comment.dto.CommentCreateRequest;
import hankyu.board.spring_board.domain.comment.dto.CommentDto;
import hankyu.board.spring_board.domain.comment.dto.CommentReadCondition;
import hankyu.board.spring_board.domain.comment.entity.Comment;
import hankyu.board.spring_board.domain.comment.repository.CommentRepository;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.post.entity.Post;
import hankyu.board.spring_board.domain.post.repository.PostRepository;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.comment.CommentNotFoundException;
import hankyu.board.spring_board.global.exception.common.CannotConvertNestedStructureException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.post.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hankyu.board.spring_board.domain.comment.dto.CommentDto.toDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final AuthUtils authUtils;

    //  postId가 같은 댓글을 모두 조회
    @Transactional(readOnly = true)
    public List<CommentDto> readAll(CommentReadCondition cond) {
        List<Comment> comments = commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId());
        return convertCommentListToDtoList(comments);
    }

    @Transactional
    public void create(CommentCreateRequest req) {
        Comment comment = buildCommentFromRequest(req);
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findWithMemberById(id).orElseThrow(CommentNotFoundException::new);
        authUtils.authorityCheck(comment.getMember().getId());
        //  commentRepository.delete()를 사용하여 삭제가능한 최상위 댓글을 삭제 -> CASCADE 설정으로 하위댓글이 일괄삭제됨.
        comment.delete().ifPresent(commentRepository::delete);
    }

    private Comment buildCommentFromRequest(CommentCreateRequest req) {
        Member member = memberRepository.findById(authUtils.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Post post = postRepository.findById(req.getPostId()).orElseThrow(PostNotFoundException::new);
        Comment parent = req.getParentId() != null
                ? commentRepository.findById(req.getParentId()).orElseThrow(CommentNotFoundException::new)
                : null;
        return new Comment(req.getContent(), member, post, parent);
    }

    //  List<Comment>를 List<CommentDto>로 변환
    private List<CommentDto> convertCommentListToDtoList(List<Comment> comments) {
        //  모든 comment를 <id, dto> 쌍으로 map에 담는다.
        Map<Long, CommentDto> commentMap = new HashMap<>();
        //  parentId가 null인 최상위 댓글만을 담을 List
        List<CommentDto> roots = new ArrayList<>();
        for (Comment comment : comments) {
            Long id = comment.getId();
            CommentDto dto = toDto(comment);
            commentMap.put(id, dto);
            Comment parent = comment.getParent();
            if(parent == null) {
                roots.add(dto);
            } else {
                try {
                    CommentDto parentDto = commentMap.get(parent.getId());
                    parentDto.getChildren().add(dto);
                } catch (NullPointerException e) {
                    throw new CannotConvertNestedStructureException(e.getMessage());
                }
            }
        }
        return roots;
    }
}