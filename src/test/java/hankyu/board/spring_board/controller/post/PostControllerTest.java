package hankyu.board.spring_board.controller.post;

import hankyu.board.spring_board.domain.post.controller.PostController;
import hankyu.board.spring_board.domain.post.dto.PostCreateRequest;
import hankyu.board.spring_board.domain.post.dto.PostReadCondition;
import hankyu.board.spring_board.domain.post.dto.PostUpdateRequest;
import hankyu.board.spring_board.domain.post.service.PostService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static hankyu.board.spring_board.factory.dto.post.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static hankyu.board.spring_board.factory.dto.post.PostReadConditionFactory.createPostReadCondition;
import static hankyu.board.spring_board.factory.dto.post.PostUpdateRequestFactory.createPostUpdateRequest;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;

    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }


    @Test
    void create_Success() throws Exception {
        //given
        ArgumentCaptor<PostCreateRequest> postCreateRequestArgumentCaptor = ArgumentCaptor.forClass(PostCreateRequest.class);

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
        );
        PostCreateRequest req = createPostCreateRequestWithImages(imageFiles);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .file("images", imageFiles.get(0).getBytes())
                                .file("images", imageFiles.get(1).getBytes())
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(postService).create(postCreateRequestArgumentCaptor.capture());
        verify(postService).create(any(PostCreateRequest.class));

        PostCreateRequest capturedRequest = postCreateRequestArgumentCaptor.getValue();

        AssertionsForClassTypes.assertThat(capturedRequest.getImages().size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(capturedRequest.getTitle()).isEqualTo(req.getTitle());
        AssertionsForClassTypes.assertThat(capturedRequest.getContent()).isEqualTo(req.getContent());
        AssertionsForClassTypes.assertThat(capturedRequest.getCategoryId()).isEqualTo(req.getCategoryId());
    }

    @Test
    void read_Success() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", id))
                .andExpect(status().isOk());
        verify(postService).read(id);
    }

    @Test
    void update_Success() throws Exception {
        // given
        ArgumentCaptor<PostUpdateRequest> postUpdateRequestArgumentCaptor = ArgumentCaptor.forClass(PostUpdateRequest.class);

        List<MultipartFile> addedImages = List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
        );
        List<Long> deletedImages = List.of(1L, 2L);

        PostUpdateRequest req = createPostUpdateRequest("title", "content",  addedImages, deletedImages);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts/{id}", 1L)
                                .file("addedImages", addedImages.get(0).getBytes())
                                .file("addedImages", addedImages.get(1).getBytes())
                                .param("deletedImageIds", String.valueOf(deletedImages.get(0)), String.valueOf(deletedImages.get(1)))
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("PUT");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(postService).update(anyLong(), postUpdateRequestArgumentCaptor.capture());

        PostUpdateRequest capturedRequest = postUpdateRequestArgumentCaptor.getValue();
        List<MultipartFile> capturedAddedImages = capturedRequest.getAddedImages();
        assertThat(capturedAddedImages.size()).isEqualTo(2);

        List<Long> capturedDeletedImages = capturedRequest.getDeletedImageIds();
        assertThat(capturedDeletedImages.size()).isEqualTo(2);
        assertThat(capturedDeletedImages).contains(deletedImages.get(0), deletedImages.get(1));
    }

    @Test
    void delete_Success() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", id))
                .andExpect(status().isOk());
        verify(postService).delete(id);
    }

    @Test
    void findAll_Success() throws Exception {
        // given
        PostReadCondition cond = createPostReadCondition(0, 1, "title", List.of(1L, 2L), 1L);

        // when, then
        mockMvc.perform(
                        get("/api/posts")
                                .param("page", String.valueOf(cond.getPage()))
                                .param("size", String.valueOf(cond.getSize()))
                                .param("keyword", cond.getKeyword())
                                .param("categoryId", String.valueOf(cond.getCategoryId().get(0)), String.valueOf(cond.getCategoryId().get(1)))
                                .param("memberId", String.valueOf(cond.getMemberId()), String.valueOf(cond.getMemberId())))
                .andExpect(status().isOk());

        verify(postService).readAll(cond);
    }

}
