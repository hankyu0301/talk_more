package hankyu.board.spring_board.factory.dto.message;

import hankyu.board.spring_board.dto.message.MessageReadCondition;

public class MessageReadConditionFactory {

    public static MessageReadCondition createMessageReadCondition() {
        return new MessageReadCondition(10, 0, null, null);
    }

    public static MessageReadCondition createMessageReadCondition(String keyword, Long targetId) {
        return new MessageReadCondition(10, 0, keyword, targetId);
    }
}
