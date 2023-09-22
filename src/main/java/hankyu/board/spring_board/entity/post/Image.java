package hankyu.board.spring_board.entity.post;

import hankyu.board.spring_board.exception.post.UnsupportedImageFormatException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private final static String supportedExtension[] = {"jpg", "jpeg", "gif", "bmp", "png"};

    public Image(String originName) {
        this.originName = originName;
        this.uniqueName = generateUniqueName(extractExtension(originName));
    }

    public void initPost(Post post) {
        if(this.post == null) {
            this.post = post;
        }
    }

    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractExtension(String originName) {
        try {
            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            if(isSupportedFormat(ext)) return ext;
        } catch (StringIndexOutOfBoundsException e) { }
        throw new UnsupportedImageFormatException();
    }

    private boolean isSupportedFormat(String ext) {
        return Arrays.stream(supportedExtension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }

}