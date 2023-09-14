package hankyu.board.spring_board.dto.email;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "이메일 인증 요청")
@Data
@AllArgsConstructor
public class EmailConfirmRequest {

    @ApiModelProperty(value = "이메일", notes = "이메일을 입력해주세요", required = true, example = "member@email.com")
    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @ApiModelProperty(value = "인증 코드", notes = "인증 코드를 입력해주세요", required = true, example = "123e4567-e89b-12d3-a456-556642440000")
    @NotBlank(message = "인증 코드를 입력해주세요.")
    private String code;
}
