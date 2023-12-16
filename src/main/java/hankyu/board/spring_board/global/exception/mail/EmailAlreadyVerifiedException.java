package hankyu.board.spring_board.global.exception.mail;

public class EmailAlreadyVerifiedException extends RuntimeException{
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
