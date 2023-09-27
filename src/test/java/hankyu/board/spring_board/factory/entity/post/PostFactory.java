package hankyu.board.spring_board.factory.entity.post;

import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Image;
import hankyu.board.spring_board.entity.post.Post;

import java.util.List;

import static hankyu.board.spring_board.factory.entity.category.CategoryFactory.createCategory;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;


public class PostFactory {
    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content",  member, category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images) {
        return new Post("title", "content",  member, category, images);
    }

    public static Post createPostWithImages(List<Image> images) {
        return new Post("title", "content",  createMember(), createCategory(), images);
    }

    public static Post createPostWithTitle(String title, Member member, Category category) {
        return new Post(title, "content",  member, category, List.of());
    }
}
