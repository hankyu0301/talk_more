package hankyu.board.spring_board.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageSimpleDto {

    private Long id;
    private String content;
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public MessageSimpleDto(Long id, String content, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}