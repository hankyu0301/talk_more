package hankyu.board.spring_board.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.dto.member.MemberDto;
import hankyu.board.spring_board.dto.member.MemberUpdateRequest;
import hankyu.board.spring_board.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hankyu.board.spring_board.factory.dto.member.MemberUpdateReuqestFactory.createMemberUpdateRequest;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMemberWithId;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    MemberController memberController;

    @Mock
    MemberService memberService;

    ObjectMapper objectMapper = new ObjectMapper();
    MockMvc mockMvc;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    //findMember, update, delete,
    @Test
    void findMember_Success() throws Exception {
        MemberDto memberDto = MemberDto.toDto(createMemberWithId(1L));
        given(memberService.findMember(memberDto.getId())).willReturn(memberDto);

        mockMvc.perform(get("/api/members/{id}", memberDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data").exists()); // Assuming data field exists in the response

        verify(memberService, times(1)).findMember(memberDto.getId());
    }

    @Test
    void update_Success() throws Exception {
        MemberDto memberDto = MemberDto.toDto(createMemberWithId(1L));
        MemberUpdateRequest req = createMemberUpdateRequest();

        mockMvc.perform(put("/api/members/{id}", memberDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(memberService, times(1)).update(memberDto.getId(), req);
    }

    @Test
    void delete_Success() throws Exception {
        MemberDto memberDto = MemberDto.toDto(createMemberWithId(1L));

        mockMvc.perform(delete("/api/members/{id}", memberDto.getId()))
                .andExpect(status().isOk());

        verify(memberService, times(1)).delete(memberDto.getId());
    }
}
