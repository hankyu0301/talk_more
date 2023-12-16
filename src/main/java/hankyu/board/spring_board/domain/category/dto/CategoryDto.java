package hankyu.board.spring_board.domain.category.dto;

import hankyu.board.spring_board.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDto {
    private Long id;
    private String name;

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        return categories.stream().map(
                category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

}