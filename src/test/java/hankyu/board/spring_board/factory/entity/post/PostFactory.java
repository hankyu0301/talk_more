package hankyu.board.spring_board.factory.entity.post;

import hankyu.board.spring_board.domain.category.entity.Category;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.post.entity.Post;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static hankyu.board.spring_board.factory.entity.category.CategoryFactory.createCategory;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.post.ImageFactory.createImageList;


public class PostFactory {
    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content",  member, category, List.of());
    }

    public static Post createPostWithImages() {
        Post post = new Post("title", "content",  createMember(), createCategory(), createImageList());
        ReflectionTestUtils.setField(post, "images", createImageList());
        return post;
    }
}
