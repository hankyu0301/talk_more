package hankyu.board.spring_board.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.dto.comment.CommentCreateRequest;
import hankyu.board.spring_board.dto.comment.CommentReadCondition;
import hankyu.board.spring_board.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hankyu.board.spring_board.factory.dto.comment.CommentCreateRequestFactory.createCommentCreateRequest;
import static hankyu.board.spring_board.factory.dto.comment.CommentReadConditionFactory.createCommentReadCondition;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @InjectMocks CommentController commentController;
    @Mock CommentService commentService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void readAll_Success() throws Exception {
        // given
        CommentReadCondition cond = createCommentReadCondition();

        // when, then
        mockMvc.perform(
                        get("/api/comments")
                                .param("postId", String.valueOf(cond.getPostId())))
                .andExpect(status().isOk());

        verify(commentService).readAll(cond);
    }

    @Test
    void createTest() throws Exception {
        // given
        CommentCreateRequest req = createCommentCreateRequest();

        // when, then
        mockMvc.perform(
                        post("/api/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(commentService).create(req);
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/comments/{id}", id))
                .andExpect(status().isOk());
        verify(commentService).delete(id);
    }

}
