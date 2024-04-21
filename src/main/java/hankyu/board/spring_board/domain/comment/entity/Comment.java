package hankyu.board.spring_board.domain.comment.entity;

import hankyu.board.spring_board.domain.common.BaseTimeEntity;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    public Comment(String content, Member member, Post post, Comment parent) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.parent = parent;
        this.deleted = false;
    }

    //삭제된 댓글임을 마킹해둠
    public void markAsDeleted() {
        this.deleted = true;
    }

    /*댓글이 삭제가능한 상태인지 확인 후 결과에 따라 다른 값을 return*/
    public Optional<Comment> delete() {
        if(deleted) {
            return Optional.empty();
        }
        this.markAsDeleted();
        /*  현재 댓글의 하위댓글이 모두 삭제된 상태인가?*/
        if(isDeletableComment()) {
            return Optional.of(findDeletableAncestorByParent());
        } return Optional.empty();
    }

    /*  삭제조건을 만족하는 최상위 댓글 반환*/
    private Comment findDeletableAncestorByParent() {
        /*  부모 댓글이 존재하고 그 댓글이 삭제되었는지?*/
        if (isDeletableParent()) {
            /*  부모 댓글에 findDeletableCommentByParent()을 재귀 호출
             *  삭제조건을 만족하는 최상위댓글을 반환함 -> 그 댓글을 삭제하면 하위 댓글도 CASCADE 설정으로 일괄 삭제됨*/
            Comment parent = getParent().findDeletableAncestorByParent();
            if(parent.isDeletableCommentForParent()) return parent;
        }
        return this;
    }

    /*  부모 댓글이 존재하고 부모 댓글의 자식댓글들이 모두 삭제된 상태인지? */
    private boolean isDeletableParent() {
        return getParent() != null && getParent().isDeleted() && getParent().isDeletableCommentForParent();
    }

    /*  마지막 댓글까지 조회하여 현재 댓글이 삭제 가능한 댓글인지 판단*/
    private boolean isDeletableComment() {
        for (Comment child : getChildren()) {
            if (!child.isDeletableComment()) {
                return false;
            }
        }
        return isDeleted();
    }

    /*  자신의 자식 레벨만 검사하는 메서드*/
    private boolean isDeletableCommentForParent() {
        for (Comment child : getChildren()) {
            if (!child.isDeleted()) {
                return false;
            }
        }
        return isDeleted();
    }

}
