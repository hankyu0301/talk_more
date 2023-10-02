package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.entity.post.Image;
import hankyu.board.spring_board.exception.post.ImageNotFoundException;
import hankyu.board.spring_board.exception.post.UnsupportedImageFormatException;
import hankyu.board.spring_board.repository.post.ImageRepository;
import hankyu.board.spring_board.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.entity.post.ImageFactory.createImage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Mock
    ImageRepository imageRepository;

    @Mock
    FileService fileService;

    @Test
    void create_Success() {
        //given
        List<MultipartFile> multipartFiles = List.of(
                new MockMultipartFile("a", "a.PNG", MediaType.IMAGE_PNG_VALUE, "a".getBytes()),
                new MockMultipartFile("b", "b.PNG", MediaType.IMAGE_PNG_VALUE, "b".getBytes()));

        //when
        multipartFiles.forEach(imageService::create);

        //then
        verify(imageRepository, times(multipartFiles.size())).save(any());
        verify(fileService, times(multipartFiles.size())).upload(any(), anyString());
    }

    @Test
    void create_UnsupportedImageFormat_ThrowsException() {
        //given
        List<MultipartFile> multipartFiles = List.of(
                new MockMultipartFile("a", "a.txt", MediaType.TEXT_PLAIN_VALUE, "a".getBytes()),
                new MockMultipartFile("b", "b.txt", MediaType.TEXT_PLAIN_VALUE, "b".getBytes()));

        //when, then
        assertThatThrownBy(() -> multipartFiles.forEach(imageService::create))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void delete_Success() {
        //given
        Image image = createImage();

        //when
        imageService.delete(image);

        //then
        verify(imageRepository).delete(any());
        verify(fileService).delete(anyString());
    }

    @Test
    void read_Success() {
        //given
        Image image = createImage();
        given(imageRepository.findById(1L)).willReturn(Optional.of(image));

        //when
        Image findImage = imageService.read(1L);

        //then
        assertThat(findImage.getId()).isEqualTo(image.getId());
    }

    @Test
    void read_ImageNotFound_ThrowsException() {
        //given
        given(imageRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> imageService.read(1L))
                .isInstanceOf(ImageNotFoundException.class);
    }
}