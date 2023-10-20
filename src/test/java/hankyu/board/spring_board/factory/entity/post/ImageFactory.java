package hankyu.board.spring_board.factory.entity.post;

import hankyu.board.spring_board.entity.post.Image;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPost;

public class ImageFactory {
    public static Image createImage() {
        return new Image("origin_filename.jpg", createPost());
    }

    public static Image createImageWithOriginName(String originName) {
        return new Image(originName, createPost());
    }
    public static List<Image> createImageList() {
        return List.of(createImageWithOriginName("a.png"), createImageWithOriginName("b.png"), createImageWithOriginName("c.png"));
    }

    public static Image createImageWithIdAndOriginName(Long id, String originName) {
        Image image = new Image(originName, createPost());
        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }
}
