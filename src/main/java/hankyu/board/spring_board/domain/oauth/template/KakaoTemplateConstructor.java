package hankyu.board.spring_board.domain.oauth.template;

import hankyu.board.spring_board.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class KakaoTemplateConstructor {

    public KakaoTemplate.Feed getWelcomeTemplate(Member member) {
        String nickname = member.getNickname();

        KakaoTemplate.Content content = KakaoTemplate.Content.builder()
                .title(String.format("%s님의 가입을 환영합니다!", nickname))
                .description("반갑습니다. talkMore 입니다.")
                // .description("PliP과 함께 여행 일정을 작성하러 가볼까요?\n*이 메시지는 타인에게 공유하지 마세요!*")
                .build();

        KakaoTemplate.Feed feed = KakaoTemplate.Feed.builder()
                .content(content)
                .button_title("TalkMore로 이동")
                .build();

        return feed;
    }

}
