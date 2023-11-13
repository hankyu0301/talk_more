package hankyu.board.spring_board;

import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.comment.Comment;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Post;
import hankyu.board.spring_board.repository.category.CategoryRepository;
import hankyu.board.spring_board.repository.comment.CommentRepository;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDB {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDB() {
        log.info("initialize database");

        initTestAdmin();
        initTestMember();
        initCategory();
        initPost();
        initComment();

        log.info("initialized database");
    }

    private void initTestAdmin() {
        Member admin = new Member("admin@admin.com", passwordEncoder.encode("123456a!"), "admin", "admin");
        admin.confirmEmail();
        admin.assignAdmin();
        memberRepository.save(admin);
    }

    private void initTestMember() {
        Member member1 = new Member("member1@member.com", passwordEncoder.encode("123456a!"), "member1", "member1");
        Member member2 = new Member("member2@member.com", passwordEncoder.encode("123456a!"), "member2", "member2");
        member1.confirmEmail();
        member2.confirmEmail();
        memberRepository.saveAll(List.of(member1,member2));
    }

    private void initCategory() {
        Category c1 = categoryRepository.save(new Category("category1"));
        Category c2 = categoryRepository.save(new Category("category2"));
        Category c3 = categoryRepository.save(new Category("category3"));
        Category c4 = categoryRepository.save(new Category("category4"));
        Category c5 = categoryRepository.save(new Category("category5"));
        Category c6 = categoryRepository.save(new Category("category6"));
        Category c7 = categoryRepository.save(new Category("category7"));
        Category c8 = categoryRepository.save(new Category("category8"));
    }

    private void initPost() {
        Member member = memberRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);
        IntStream.range(0, 100)
                .forEach(i -> postRepository.save(
                        new Post("title" + i, "content" + i, member, category)
                ));
    }

    private void initComment() {
        Member member = memberRepository.findAll().get(0);
        Post post = postRepository.findAll().get(0);
        Comment c1 = commentRepository.save(new Comment("content", member, post, null));
        Comment c2 = commentRepository.save(new Comment("content", member, post, c1));
        Comment c3 = commentRepository.save(new Comment("content", member, post, c1));
        Comment c4 = commentRepository.save(new Comment("content", member, post, c2));
        Comment c5 = commentRepository.save(new Comment("content", member, post, c2));
        Comment c6 = commentRepository.save(new Comment("content", member, post, c4));
        Comment c7 = commentRepository.save(new Comment("content", member, post, c3));
        Comment c8 = commentRepository.save(new Comment("content", member, post, null));
    }

}