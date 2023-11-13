package hankyu.board.spring_board.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.dto.message.MessageCreateRequest;
import hankyu.board.spring_board.dto.message.MessageDeleteRequest;
import hankyu.board.spring_board.dto.message.MessageReadCondition;
import hankyu.board.spring_board.service.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static hankyu.board.spring_board.factory.dto.message.MessageCreateRequestFactory.createMessageCreateRequest;
import static hankyu.board.spring_board.factory.dto.message.MessageDeleteRequestFactory.createMessageDeleteRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @InjectMocks
    MessageController messageController;
    @Mock
    MessageService messageService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    void readAllSentMessageByCond_Success() throws Exception {
        // given
        Long targetId = 1L;
        Integer size = 2;
        Integer page = 0;
        String keyword = "keyword";

        // when, then
        mockMvc.perform(
                        get("/api/messages/sender")
                                .param("size", String.valueOf(size))
                                .param("page", String.valueOf(page))
                                .param("keyword", keyword)
                                .param("targetId", String.valueOf(targetId)))
                .andExpect(status().isOk());

        verify(messageService).readAllSentMessageByCond(any(MessageReadCondition.class));
    }

    @Test
    void readAllReceivedMessageByCond_Success() throws Exception {
        // given
        Long targetId = 1L;
        Integer size = 2;
        Integer page = 0;
        String keyword = "keyword";

        // when, then
        mockMvc.perform(
                        get("/api/messages/receiver")
                                .param("size", String.valueOf(size))
                                .param("page", String.valueOf(page))
                                .param("keyword", keyword)
                                .param("targetId", String.valueOf(targetId)))
                .andExpect(status().isOk());

        verify(messageService).readAllReceivedMessageByCond(any(MessageReadCondition.class));
    }

    @Test
    void read_Success() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/messages/{id}", id))
                .andExpect(status().isOk());
        verify(messageService).read(id);
    }

    @Test
    void createTest() throws Exception {
        // given
        MessageCreateRequest req = createMessageCreateRequest();

        // when, then
        mockMvc.perform(
                        post("/api/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(messageService).create(req);
    }

    @Test
    void deleteBySender_Success() throws Exception {
        // given
        List<Long> deletedMessageIds = List.of(1L,2L);
        MessageDeleteRequest req = createMessageDeleteRequest(deletedMessageIds);

        // when, then
        mockMvc.perform(
                delete("/api/messages/sender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(messageService).deleteBySender(req);
    }
    @Test
    void deleteByReceiver_Success() throws Exception {
        // given
        List<Long> deletedMessageIds = List.of(1L,2L);
        MessageDeleteRequest req = createMessageDeleteRequest(deletedMessageIds);

        // when, then
        mockMvc.perform(
                        delete("/api/messages/receiver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(messageService).deleteByReceiver(req);
    }
}
