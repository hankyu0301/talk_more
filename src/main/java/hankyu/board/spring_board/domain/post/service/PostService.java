package hankyu.board.spring_board.domain.post.service;

import hankyu.board.spring_board.domain.category.entity.Category;
import hankyu.board.spring_board.domain.category.repository.CategoryRepository;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.post.dto.*;
import hankyu.board.spring_board.domain.post.entity.Image;
import hankyu.board.spring_board.domain.post.entity.Post;
import hankyu.board.spring_board.domain.post.repository.PostRepository;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.post.PostNotFoundException;
import hankyu.board.spring_board.global.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final AuthUtils authUtils;

    /*  QueryDSL로 작성한 메서드*/
    @Transactional(readOnly = true)
    public PostListDto readAll(PostReadCondition cond) {
        return PostListDto.toDto(
                postRepository.findAllByCondition(cond)
        );
    }

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        Member member = memberRepository.findById(authUtils.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        List<Image> images = req.getImages().stream().map(i -> new Image(i.getOriginalFilename())).collect(toList());

        Post post = postRepository.save(
                new Post(req.getTitle(), req.getContent(), member, category, images)
        );
        uploadImages(post.getImages(), req.getImages());
        return new PostCreateResponse(post.getId());
    }

    public PostDto read(Long id) {
        return PostDto.toDto(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        authUtils.authorityCheck(post.getMember().getId());
        deleteImages(post.getImages());
        postRepository.delete(post);
    }

    @Transactional
    public PostUpdateResponse update(Long id, PostUpdateRequest req) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        authUtils.authorityCheck(post.getMember().getId());
        Post.ImageUpdatedResult result = post.update(req);
        uploadImages(result.getAddedImages(), result.getAddedImageFiles());
        deleteImages(result.getDeletedImages());
        return new PostUpdateResponse(id);
    }

    private void uploadImages(List<Image> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deleteImages(List<Image> images) {
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }
}