package hankyu.board.spring_board.factory.dto.message;

import hankyu.board.spring_board.dto.message.MessageCreateRequest;

public class MessageCreateRequestFactory {

    public static MessageCreateRequest createMessageCreateRequest() {
        return new MessageCreateRequest("content", 1L);
    }
}
