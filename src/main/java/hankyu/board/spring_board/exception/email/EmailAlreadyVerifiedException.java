package hankyu.board.spring_board.exception.email;

public class EmailAlreadyVerifiedException extends RuntimeException{
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
