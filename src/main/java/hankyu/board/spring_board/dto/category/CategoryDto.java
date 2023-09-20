package hankyu.board.spring_board.dto.category;

import hankyu.board.spring_board.entity.category.Category;
import hankyu.board.spring_board.helper.HierarchyConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDto {
    private Long id;
    private String name;
    private List<CategoryDto> children;

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        HierarchyConverter<Category, CategoryDto> converter = HierarchyConverter.create(
                categories,
                category -> new CategoryDto(category.getId(), category.getName(), new ArrayList<>()),
                Category::getId,
                category -> category.getParent() == null ? null : category.getParent().getId(),
                CategoryDto::getChildren);
        return converter.convertToHierarchy();
    }

}