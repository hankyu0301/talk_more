package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.dto.post.*;
import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Image;
import hankyu.board.spring_board.entity.post.Post;
import hankyu.board.spring_board.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.exception.post.PostNotFoundException;
import hankyu.board.spring_board.repository.category.CategoryRepository;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.post.PostRepository;
import hankyu.board.spring_board.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {

        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        List<Image> imageList = req.getImages().stream()
                .map(i -> new Image(i.getOriginalFilename()))
                .collect(Collectors.toList());
        Post post = postRepository.save(new Post(req.getTitle(), req.getContent(), member, category, imageList));
        uploadImages(req.getImages(), post.getImages());
        return new PostCreateResponse(post.getId());
    }

    @Transactional(readOnly = true)
    public PostDto read(Long id) {
        Post post = postRepository.findByIdWithMember(id).orElseThrow(PostNotFoundException::new);
        return PostDto.toDto(post);
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        deleteImages(post.getImages());
        postRepository.delete(post);
    }

    @Transactional
    public PostUpdateResponse update(Long id, PostUpdateRequest req) {
        Post post = postRepository.findByIdWithMember(id).orElseThrow(PostNotFoundException::new);

        // 이미지 엔티티의 연관관계를 정리함. 미리 작성해둔 설정으로 인해 연관 관계가 끊어지면 삭제.
        Post.ImageUpdatedResult result = post.update(req);

        // 이미지 파일을 fileService로 관리
        uploadImages(result.getAddedImageFiles(), result.getAddedImages());
        deleteImages(result.getDeletedImages());

        return new PostUpdateResponse(id);
    }

    private void uploadImages(List<MultipartFile> fileImages, List<Image> images) {
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deleteImages(List<Image> images) {
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }

    public PostListDto readAll(PostReadCondition cond) {
        return PostListDto.toDto(
                postRepository.findAllByCondition(cond)
        );
    }
}
