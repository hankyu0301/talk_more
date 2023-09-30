package hankyu.board.spring_board.entity.post;

import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.entity.member.Member;
import hankyu.board.spring_board.exception.post.UnsupportedImageFormatException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ImageTest {

    @Test
    void create_Success() {
        //given
        String validExtension = "JPEG";

        //when, then
        Image image = new Image("image." + validExtension);
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

    @Test
    void init_Test_Success() {
        //given
        Image image = new Image("image." + "JPEG");
        Member member = new Member("finebears@naver.com", "123456a!", "장한규","finebears");
        Category category = new Category("name1");

        //when
        Post post = new Post("title", "content", member, category, List.of());
        image.initPost(post);

        //then
        assertThat(image.getPost()).isSameAs(post);
    }

    @Test
    void init_Immutable_Test_Success() {
        //given
        Image image = new Image("image." + "JPEG");
        Member member = new Member("finebears@naver.com", "123456a!", "장한규","finebears");
        Category category = new Category("name1");
        image.initPost(new Post("title1", "content1", member, category, List.of()));

        //when
        Post post = new Post("title1", "content1", member, category, List.of());
        image.initPost(post);

        //then
        assertThat(image.getPost()).isNotSameAs(post);
    }
}

