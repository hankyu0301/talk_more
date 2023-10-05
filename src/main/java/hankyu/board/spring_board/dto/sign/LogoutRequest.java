package hankyu.board.spring_board.dto.sign;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {
    @ApiModelProperty(value = "액세스 토큰", notes = "액세스 토큰을 입력해주세요", required = true)
    @NotBlank(message = "액세스 토큰을 입력해주세요.")
    private String accessToken;

    @ApiModelProperty(value = "리프레시 토큰", notes = "리프레시 토큰을 입력해주세요", required = true)
    @NotBlank(message = "리프레시 토큰을 입력해주세요.")
    private String refreshToken;
}
