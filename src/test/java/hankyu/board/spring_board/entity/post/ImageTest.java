package hankyu.board.spring_board.entity.post;

import hankyu.board.spring_board.domain.post.entity.Image;
import hankyu.board.spring_board.global.exception.post.UnsupportedImageFormatException;
import org.junit.jupiter.api.Test;

import static hankyu.board.spring_board.factory.entity.post.ImageFactory.createImage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ImageTest {

    @Test
    void create_Success() {
        //given
        String validExtension = "JPEG";

        //when, then
        Image image = createImage();
    }

    @Test
    void create_UnsupportedFormat_ThrowsException() {
        //given
        String invalidExtension = "invalid";

        //when, then
        assertThatThrownBy( () -> new Image("image." + invalidExtension))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void create_UnsupportedFormat_ThrowsException2() {
        //given
        String originName = "image";

        //when, then
        assertThatThrownBy( () -> new Image(originName))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

}

