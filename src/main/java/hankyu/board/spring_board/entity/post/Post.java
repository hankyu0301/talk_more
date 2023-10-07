package hankyu.board.spring_board.entity.post;

import hankyu.board.spring_board.dto.post.PostUpdateRequest;
import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.common.BaseTimeEntity;
import hankyu.board.spring_board.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @OneToMany(mappedBy = "post")
    private List<Image> images;

    public Post(String title, String content, Member member, Category category) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
    }

    public void update(PostUpdateRequest req) {
        this.title = req.getTitle();
        this.content = req.getContent();
    }

}
