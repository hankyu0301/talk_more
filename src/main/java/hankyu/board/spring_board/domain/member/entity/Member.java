package hankyu.board.spring_board.domain.member.entity;

import hankyu.board.spring_board.domain.common.BaseTimeEntity;
import hankyu.board.spring_board.domain.member.dto.MemberDto;
import hankyu.board.spring_board.domain.member.dto.MemberUpdateRequest;
import hankyu.board.spring_board.domain.oauth.entity.KakaoToken;
import hankyu.board.spring_board.global.event.sign.MemberCreateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String email;

    private String password;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private KakaoToken kakaoToken;

    public Member(String email, String password, String username, String nickname) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.memberRole = MemberRole.ROLE_NORMAL;
        this.enabled = false;
    }

    public Member(String email, String nickname) {
        this.email = email;
        this.username = nickname;
        this.nickname = nickname;
        this.memberRole = MemberRole.ROLE_SOCIAL;
        this.enabled = true;
    }

    public Member update(MemberUpdateRequest req) {
        this.username = req.getUsername();
        this.nickname = req.getNickname();
        return this;
    }

    public void assignAdmin() {
        this.memberRole = MemberRole.ROLE_ADMIN;
    }

    public void publishCreatedEvent(ApplicationEventPublisher publisher) {
        publisher.publishEvent(
                new MemberCreateEvent(MemberDto.toDto(this))
        );
    }

    public void confirmEmail() {
        this.enabled = true;
    }
}
