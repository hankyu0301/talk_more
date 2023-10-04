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

    public Optional<Comment> findDeletableComment() {
        this.deleted = true;
        return isDeletableComment() ? Optional.of(findDeletableCommentByParent()) : Optional.empty();
    }

    public void delete() {
        this.deleted = true;
    }

    private Comment findDeletableCommentByParent() {
        /*  부모 댓글이 존재하고 그 댓글이 삭제되었는지?*/
        if (isDeletedParent()) {
            /*  부모 댓글에 findDeletableCommentByParent()을 재귀 호출
             *   isDeletedParent()를 만족하지 않는 댓글까지 방문(top)
             *   -> top에서 내려오면 삭제가능한 댓글 중 가장 상위댓글을 찾고
             *   상위 댓글부터 하위 댓글이 모두 삭제된 상태인지 확인함.
             *   삭제된 상태면 해당 댓글 반환 : 삭제된 상태가 아니면 하위 댓글로 이동 후 확인 반복 */
            Comment deletableParent = getParent().findDeletableCommentByParent();
            if(getParent().isDeletableComment()) return deletableParent;
        }
        return this;
    }

    /*  자식 댓글이 존재하는지?*/
    private boolean hasChildren() {
        return getChildren().size() != 0;
    }

    /*  부모 댓글이 존재하고 그 댓글이 삭제되었는지?*/
    private boolean isDeletedParent() {
        return getParent() != null && getParent().isDeleted();
    }

    private boolean isDeletableComment() {
        /*  자식 댓글이 삭제되었는지 확인*/
        for (Comment child : getChildren()) {
            /*  삭제되지 않은 자식 댓글이 있다면 바로 return*/
            if(!child.isDeleted()) {
                return false;
            }
            /*  자식 댓글이 삭제되었다면 최하위 댓글까지 조회*/
            if(child.hasChildren()){
                child.isDeletableComment();
            }

        }
        return true;
    }

    public boolean isEqualMember(Member member) {
        return this.member.equals(member);
    }
}
