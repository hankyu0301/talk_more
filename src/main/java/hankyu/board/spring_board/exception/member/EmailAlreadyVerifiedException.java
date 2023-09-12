package hankyu.board.spring_board.exception.member;

public class EmailAlreadyVerifiedException extends RuntimeException{
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
