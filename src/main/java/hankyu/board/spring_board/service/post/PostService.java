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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static hankyu.board.spring_board.helper.AuthHelper.extractMemberId;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public PostListDto readAll(PostReadCondition cond) {
        return PostListDto.toDto(
                postRepository.findAllByCondition(cond)
        );
    }

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        Long memberId = extractMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        List<Image> imageList = req.getImages().stream().map(imageService::create).collect(Collectors.toList());
        Post post = postRepository.save(new Post(req.getTitle(), req.getContent(), member, category, imageList));
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
    public PostUpdateResponse update(Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findByIdWithMember(id).orElseThrow(PostNotFoundException::new);
        ImageUpdateResult imageUpdateResult = updateImages(postUpdateRequest.getAddedImages(), postUpdateRequest.getDeletedImageIds());
        post.update(postUpdateRequest, imageUpdateResult);
        return new PostUpdateResponse(id);
    }

    /*  전달받은 이미지 수정요청을 처리하고 저장한 이미지, 삭제한 이미지 반 */
    private ImageUpdateResult updateImages(List<MultipartFile> addedImages, List<Long> deletedImageIds) {
        List<Image> uploadResult =  uploadImages(addedImages);
        List<Image> deletedImages = convertImageIdsToImages(deletedImageIds);
        deleteImages(deletedImages);
        return new ImageUpdateResult(uploadResult, deletedImages);
    }

    /*  삭제할 이미지의 id를 imageService.read(id)에서 Image로 변환*/
    private List<Image> convertImageIdsToImages(List<Long> deletedImageIds) {
        return deletedImageIds.stream().map(imageService::read).collect(Collectors.toList());
    }

    /*  업로드 할 사진파일을 imageService.create(MultipartFile file)에서 이미지 엔티티 저장, 파일을 저장*/
    private List<Image> uploadImages(List<MultipartFile> fileImages) {
        return fileImages.stream().map(imageService::create).collect(Collectors.toList());
    }

    /*  삭제할 image를 imageService.delete(Image image)에서 이미지 엔티티 삭제, 파일 삭제*/
    private void deleteImages(List<Image> images) {
        images.forEach(imageService::delete);
    }
}
