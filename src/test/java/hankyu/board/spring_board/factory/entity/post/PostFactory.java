package hankyu.board.spring_board.factory.entity.post;

import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Post;
import org.springframework.test.util.ReflectionTestUtils;

import static hankyu.board.spring_board.factory.entity.category.CategoryFactory.createCategory;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.post.ImageFactory.createImageList;


public class PostFactory {
    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content",  member, category);
    }

    public static Post createPostWithImages() {
        Post post = new Post("title", "content",  createMember(), createCategory());
        ReflectionTestUtils.setField(post, "images", createImageList());
        return post;
    }
}
