package hankyu.board.spring_board.entity.comment;

import hankyu.board.spring_board.entity.common.BaseTimeEntity;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Post;
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
        /*  현재 댓글의 하위댓글이 모두 삭제된 상태인가?*/
        return isDeletableComment()
                ? Optional.of(findDeletableAncestorByParent())
                : Optional.empty();
    }

    /*  삭제조건을 만족하는 최상위 댓글 반환*/
    private Comment findDeletableAncestorByParent() {
        /*  부모 댓글이 존재하고 그 댓글이 삭제되었는지?*/
        if (isDeletableParent()) {
            /*  부모 댓글에 findDeletableCommentByParent()을 재귀 호출
             *  삭제조건을 만족하는 최상위댓글을 반환함 -> 그 댓글을 삭제하면 하위 댓글도 CASCADE 설정으로 일괄 삭제됨*/
            return getParent().findDeletableAncestorByParent();
        }
        return this;
    }

    /*  부모 댓글이 존재하고 자식 댓글들이 모두 삭제된 상태인지? */
    private boolean isDeletableParent() {
        return getParent() != null && getParent().isDeletableComment();
    }

    private boolean isDeletableComment() {
        /*  자식 댓글이 삭제되었는지 확인*/
        for (Comment child : getChildren()) {
            /*  삭제되지 않은 자식 댓글이 있다면 바로 return*/
            if(!child.isDeleted()) {
                return false;
            }
        }
        return true;
    }

}
