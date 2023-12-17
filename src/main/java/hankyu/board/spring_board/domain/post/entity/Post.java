package hankyu.board.spring_board.domain.post.entity;

import hankyu.board.spring_board.domain.category.entity.Category;
import hankyu.board.spring_board.domain.common.BaseTimeEntity;
import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.post.dto.PostUpdateRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public ImageUpdatedResult update(PostUpdateRequest req) {
        this.title = req.getTitle();
        this.content = req.getContent();
        ImageUpdatedResult result = findImageUpdatedResult(req.getAddedImages(), req.getDeletedImageIds());
        addImages(result.getAddedImages());
        deleteImages(result.getDeletedImages());
        return result;
    }

    private void addImages(List<Image> added) {
        added.forEach(i -> {
            images.add(i);
            i.initPost(this);
        });
    }

    private void deleteImages(List<Image> deleted) {
        deleted.forEach(di -> this.images.remove(di));
    }

    private ImageUpdatedResult findImageUpdatedResult(List<MultipartFile> addedImageFiles, List<Long> deletedImageIds) {
        List<Image> addedImages = convertImageFilesToImages(addedImageFiles);
        List<Image> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdatedResult(addedImageFiles, addedImages, deletedImages);
    }

    private List<Image> convertImageIdsToImages(List<Long> imageIds) {
        return imageIds.stream()
                .map(this::convertImageIdToImage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private Optional<Image> convertImageIdToImage(Long id) {
        return this.images.stream().filter(i -> i.getId().equals(id)).findAny();
    }

    private List<Image> convertImageFilesToImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream().map(imageFile -> new Image(imageFile.getOriginalFilename())).collect(toList());
    }

    @Getter
    @AllArgsConstructor
    public static class ImageUpdatedResult {
        private List<MultipartFile> addedImageFiles;
        private List<Image> addedImages;
        private List<Image> deletedImages;
    }
}