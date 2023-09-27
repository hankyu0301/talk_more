package hankyu.board.spring_board.service.category;

import hankyu.board.spring_board.dto.category.CategoryCreateRequest;
import hankyu.board.spring_board.dto.category.CategoryDto;
import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryDto.toDtoList(categories);
    }

    @Transactional
    public void createCategory(CategoryCreateRequest req) {
        categoryRepository.save(new Category(req.getName()));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }
}
