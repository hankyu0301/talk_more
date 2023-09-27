package hankyu.board.spring_board.controller.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.dto.category.CategoryCreateRequest;
import hankyu.board.spring_board.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hankyu.board.spring_board.factory.dto.category.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryService categoryService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }


    @Test
    void readAll_Success() throws Exception {
        // given

        // when, then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        verify(categoryService).findAllCategories();
    }

    @Test
    void create_Success() throws Exception {
        // given
        CategoryCreateRequest req = createCategoryCreateRequest();

        // when, then
        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(categoryService).createCategory(req);
    }

    @Test
    void delete_Success() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/categories/{id}", id))
                .andExpect(status().isOk());
        verify(categoryService).deleteCategory(id);
    }
}
