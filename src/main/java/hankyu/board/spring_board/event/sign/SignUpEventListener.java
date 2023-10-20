package hankyu.board.spring_board.event.sign;

import hankyu.board.spring_board.service.email.EmailService;
import hankyu.board.spring_board.service.redis.RedisKey;
import hankyu.board.spring_board.service.redis.RedisService;
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
    private final RedisService redisService;

    @TransactionalEventListener
    @Async
    public void handleAlarm(SignUpEvent event) {
        String email = event.getCreatedMember().getEmail();
        String verificationCode = emailService.sendEmail(email);
        redisService.setDataWithExpiration(RedisKey.EMAIL, verificationCode, email, 60*5L);
    }
}