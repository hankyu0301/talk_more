package hankyu.board.spring_board.dto.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "회원 탈퇴 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteRequest {

    @ApiModelProperty(value = "액세스 토큰", notes = "액세스 토큰을 입력해주세요", required = true)
    @NotBlank(message = "액세스 토큰을 입력해주세요.")
    private String accessToken;
}
