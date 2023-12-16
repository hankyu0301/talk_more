package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.domain.post.repository.ImageRepository;
import hankyu.board.spring_board.domain.post.service.ImageService;
import hankyu.board.spring_board.global.exception.post.ImageNotFoundException;
import hankyu.board.spring_board.global.exception.post.UnsupportedImageFormatException;
import hankyu.board.spring_board.global.file.FileService;
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
import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPost;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        multipartFiles.forEach(mf -> imageService.create(mf, createPost()));

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
        assertThatThrownBy(() -> multipartFiles.forEach(mf -> imageService.create(mf, createPost())))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void delete_Success() {
        //given
        given(imageRepository.findById(anyLong())).willReturn(Optional.of(createImage()));

        //when
        imageService.delete(anyLong());

        //then
        verify(imageRepository).delete(any());
        verify(fileService).delete(anyString());
    }

    @Test
    void delete_ImageNotFound_ThrowsException() {
        //given
        given(imageRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> imageService.delete(anyLong()))
                .isInstanceOf(ImageNotFoundException.class);
    }

    @Test
    void deleteAll_Success() {
        //given
        given(imageRepository.findById(anyLong())).willReturn(Optional.of(createImage()));

        //when
        imageService.delete(anyLong());

        //then
        verify(imageRepository).delete(any());
        verify(fileService).delete(anyString());
    }

}
