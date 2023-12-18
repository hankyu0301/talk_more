package hankyu.board.spring_board.controller.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.domain.mail.controller.EmailController;
import hankyu.board.spring_board.domain.mail.dto.EmailConfirmRequest;
import hankyu.board.spring_board.domain.mail.dto.ResendEmailRequest;
import hankyu.board.spring_board.domain.mail.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hankyu.board.spring_board.factory.dto.email.EmailAuthRequestFactory.createEmailAuthRequest;
import static hankyu.board.spring_board.factory.dto.email.ResendEmailRequestFactory.createResendEmailRequest;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @InjectMocks
    EmailController emailController;

    @Mock
    EmailService emailService;

    ObjectMapper objectMapper = new ObjectMapper();
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
    }


    @Test
    void confirmEmail_Success() throws Exception {
        //given
        EmailConfirmRequest req = createEmailAuthRequest();

        mockMvc.perform(get("/api/email?email={email}&code={code}", req.getEmail(), req.getCode()))
                .andExpect(status().isOk());

        verify(emailService, times(1)).confirmEmail(req);
    }

    @Test
    void resend_Success() throws Exception {
        //given
        ResendEmailRequest req = createResendEmailRequest();

        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(emailService, times(1)).resend(req);
    }
}
