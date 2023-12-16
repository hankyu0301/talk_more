package hankyu.board.spring_board.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageListDto {
    private Long totalElements;
    private Integer totalPages;
    private boolean hasNext;
    private List<MessageSimpleDto> messageList;

    public static MessageListDto toDto(Page<MessageSimpleDto> page) {
        return new MessageListDto(page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.getContent());
    }
}