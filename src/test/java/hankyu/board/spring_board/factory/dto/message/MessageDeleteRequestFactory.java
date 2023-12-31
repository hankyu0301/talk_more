package hankyu.board.spring_board.factory.dto.message;

import hankyu.board.spring_board.domain.message.dto.MessageDeleteRequest;

import java.util.List;

public class MessageDeleteRequestFactory {
    public static MessageDeleteRequest createMessageDeleteRequest(List<Long> ids) {
        return new MessageDeleteRequest(ids);
    }
}
