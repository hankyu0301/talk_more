package hankyu.board.spring_board.controller.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.dto.sign.LogoutRequest;
import hankyu.board.spring_board.dto.sign.SignInRequest;
import hankyu.board.spring_board.dto.sign.SignUpRequest;
import hankyu.board.spring_board.dto.token.TokenReissueRequest;
import hankyu.board.spring_board.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hankyu.board.spring_board.factory.dto.sign.LogoutRequestFactory.createLogoutRequest;
import static hankyu.board.spring_board.factory.dto.sign.SignInRequestFactory.createSignInRequest;
import static hankyu.board.spring_board.factory.dto.sign.SignUpRequestFactory.createSignUpRequest;
import static hankyu.board.spring_board.factory.dto.sign.TokenReissueRequestFactory.createTokenReissueRequest;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SignControllerTest {

    @InjectMocks
    SignController signController;

    @Mock
    SignService signService;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(signController).build();
    }


    @Test
    void signUp_Success() throws Exception {
        //given
        SignUpRequest req = createSignUpRequest();

        mockMvc.perform(post("/api/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(signService, times(1)).signUp(req);
    }

    @Test
    void signIn_Success() throws Exception {
        //given
        SignInRequest req = createSignInRequest();

        mockMvc.perform(post("/api/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(signService, times(1)).signIn(req);
    }

    @Test
    void logout_Success() throws Exception {
        //given
        LogoutRequest req = createLogoutRequest();

        mockMvc.perform(post("/api/log-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(signService, times(1)).logout(req);
    }

    @Test
    void reissue_Success() throws Exception {
        //given
        TokenReissueRequest req = createTokenReissueRequest();

        mockMvc.perform(post("/api/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(signService, times(1)).reissue(req);
    }
}