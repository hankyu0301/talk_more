package hankyu.board.spring_board.factory.entity.message;

import hankyu.board.spring_board.domain.member.entity.Member;
import hankyu.board.spring_board.domain.message.entity.Message;

import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;

public class MessageFactory {
    public static Message createMessage() {
        return new Message("content", createMember(), createMember());
    }

    public static Message createMessage(Member sender, Member receiver) {
        return new Message("content", sender, receiver);
    }

    public static Message createMessage(String content, Member sender, Member receiver) {
        return new Message(content, sender, receiver);
    }
}
