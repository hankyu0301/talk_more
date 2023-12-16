package hankyu.board.spring_board.global.event.sign;

import hankyu.board.spring_board.domain.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpEventListener {

    private final EmailService emailService;

    @TransactionalEventListener
    @Async
    public void handleAlarm(SignUpEvent event) {
        String email = event.getCreatedMember().getEmail();
        emailService.sendEmail(email);
    }
}