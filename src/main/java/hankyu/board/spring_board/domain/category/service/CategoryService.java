package hankyu.board.spring_board.domain.category.service;

import hankyu.board.spring_board.domain.category.dto.CategoryCreateRequest;
import hankyu.board.spring_board.domain.category.dto.CategoryDto;
import hankyu.board.spring_board.domain.category.entity.Category;
import hankyu.board.spring_board.domain.category.repository.CategoryRepository;
import hankyu.board.spring_board.global.exception.category.CategoryNotFoundException;
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
