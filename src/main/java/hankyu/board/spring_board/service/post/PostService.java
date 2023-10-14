package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.auth.AuthChecker;
import hankyu.board.spring_board.dto.post.*;
import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.entity.post.Post;
import hankyu.board.spring_board.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.exception.post.PostNotFoundException;
import hankyu.board.spring_board.repository.category.CategoryRepository;
import hankyu.board.spring_board.repository.member.MemberRepository;
import hankyu.board.spring_board.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final AuthChecker authChecker;

    @Transactional(readOnly = true)
    public PostListDto readAll(PostReadCondition cond) {
        return PostListDto.toDto(
                postRepository.findAllByCondition(cond)
        );
    }

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        Member member = memberRepository.findById(authChecker.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        //Image Entity의 저장 및 실제 파일 업로드
        createAndSaveImage(req.getImages());
        Post post = postRepository.save(new Post(req.getTitle(), req.getContent(), member, category));
        return new PostCreateResponse(post.getId());
    }

    @Transactional(readOnly = true)
    public PostDto read(Long id) {
        Post post = postRepository.findByIdWithMemberAndImages(id).orElseThrow(PostNotFoundException::new);
        return PostDto.toDto(post);
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findByIdWithMemberAndImages(id).orElseThrow(PostNotFoundException::new);
        authChecker.authorityCheck(post.getMember().getId());
        imageService.deleteAll(post.getImages());
        postRepository.delete(post);
    }


    @Transactional
    public PostUpdateResponse update(Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findByIdWithMemberAndImages(id).orElseThrow(PostNotFoundException::new);
        authChecker.authorityCheck(post.getMember().getId());
        updateImages(postUpdateRequest.getAddedImages(), postUpdateRequest.getDeletedImageIds());
        post.update(postUpdateRequest);
        return new PostUpdateResponse(id);
    }

    /*  전달받은 이미지 수정요청을 처리 */
    private void updateImages(List<MultipartFile> addedImages, List<Long> deletedImageIds) {
        //  이미지 업로드
        uploadImages(addedImages);
        deleteImages(deletedImageIds);
    }
    private void createAndSaveImage(List<MultipartFile> images) {
        images.forEach(imageService::create);
    }

    /*  업로드 할 사진파일을 imageService.create(MultipartFile file)에서 이미지 엔티티 저장, 파일을 저장*/
    private void uploadImages(List<MultipartFile> fileImages) {
        fileImages.forEach(imageService::create);
    }

    /*  삭제할 image를 imageService.delete(Long id)에서 이미지 엔티티 삭제, 파일 삭제*/
    private void deleteImages(List<Long> imageIds) {
        imageIds.forEach(imageService::delete);
    }


}
