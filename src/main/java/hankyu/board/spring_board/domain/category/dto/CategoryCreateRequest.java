package hankyu.board.spring_board.domain.category.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(value = "카테고리 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    @ApiModelProperty(value = "카테고리 명", notes = "카테고리 명을 입력하세요.", required = true, example = "category 1")
    @NotBlank(message = "카테고리 명을 입력하세요.")
    @Size(min = 2, max = 15, message = "길이 제한은 2~15자 이내입니다.")
    private String name;

}
