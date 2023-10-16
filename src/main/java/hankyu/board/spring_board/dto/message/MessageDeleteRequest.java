package hankyu.board.spring_board.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "쪽지 삭제 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDeleteRequest {

    @ApiModelProperty(value = "제거된 메세지 아이디", notes = "제거될 메세지 아이디를 입력해주세요.")
    @NotNull(message = "제거될 메세지 아이디를 입력해주세요.")
    private List<Long> deletedMessageIds;
}