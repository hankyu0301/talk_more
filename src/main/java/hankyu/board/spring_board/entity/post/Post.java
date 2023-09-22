package hankyu.board.spring_board.entity.post;

import hankyu.board.spring_board.dto.post.PostUpdateRequest;
import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.common.BaseTimeEntity;
import hankyu.board.spring_board.entity.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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

    /* cascadeType.PERSIST 설정으로 post 저장시 연관관계 설정 되어있는 Image도 영속처리됨.
    *  orphanRemoval = true 설정으로 post 삭제시 연관관계 설정 되어있는 Image도 삭제처리됨.
    * */
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images;

    public Post(String title, String content, Member member, Category category, List<Image> images) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
        addImages(images);
    }

    private void addImages(List<Image> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initPost(this);
        });
    }

    private void deleteImages(List<Image> deleted) {
        deleted.stream().forEach(di -> this.images.remove(di));
    }

    public ImageUpdatedResult update(PostUpdateRequest req) {
        this.title = req.getTitle();
        this.content = req.getContent();

        ImageUpdatedResult result = findImageUpdatedResult(req.getAddedImages(), req.getDeletedImages());
        addImages(result.getAddedImages());
        deleteImages(result.getDeletedImages());

        return result;
    }

    private ImageUpdatedResult findImageUpdatedResult(List<MultipartFile> addedImageFiles, List<Long> deletedImageIds) {
        List<Image> addedImages = convertImageFilesToImages(addedImageFiles);
        List<Image> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdatedResult(addedImageFiles, addedImages, deletedImages);
    }

    private List<Image> convertImageFilesToImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream().map(imageFile -> new Image(imageFile.getOriginalFilename())).collect(toList());
    }

    private List<Image> convertImageIdsToImages(List<Long> imageIds) {
        return imageIds.stream()
                .map(this::convertImageIdToImage)
                .flatMap(Optional::stream)
                .collect(toList());
    }

    private Optional<Image> convertImageIdToImage(Long id) {
        return this.images.stream().filter(i -> i.getId().equals(id)).findAny();
    }

    @Getter
    @AllArgsConstructor
    public static class ImageUpdatedResult {
        private List<MultipartFile> addedImageFiles;
        private List<Image> addedImages;
        private List<Image> deletedImages;
    }
}
