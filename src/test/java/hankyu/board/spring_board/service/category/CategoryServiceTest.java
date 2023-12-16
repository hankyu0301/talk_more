package hankyu.board.spring_board.service.category;

import hankyu.board.spring_board.domain.category.dto.CategoryCreateRequest;
import hankyu.board.spring_board.domain.category.dto.CategoryDto;
import hankyu.board.spring_board.domain.category.entity.Category;
import hankyu.board.spring_board.domain.category.repository.CategoryRepository;
import hankyu.board.spring_board.domain.category.service.CategoryService;
import hankyu.board.spring_board.global.exception.category.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.category.CategoryCreateRequestFactory.createCategoryCreateRequest;
import static hankyu.board.spring_board.factory.entity.category.CategoryFactory.createCategory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    void findAll_Success() {
        // given
        List<Category> categories = new ArrayList<>();
        categories.add(createCategory());
        given(categoryRepository.findAll()).willReturn(categories);

        // when
        List<CategoryDto> result = categoryService.findAllCategories();

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void create_Success() {
        // given
        CategoryCreateRequest req = createCategoryCreateRequest();

        // when
        categoryService.createCategory(req);

        // then
        verify(categoryRepository).save(any());
    }

    @Test
    void deleteTest_Success() {
        // given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(createCategory()));

        // when
        categoryService.deleteCategory(anyLong());

        // then
        verify(categoryRepository).delete(any());
    }

    @Test
    void delete_CategoryNotFound_ThrowsException() {
        // given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(anyLong()));
    }
}
