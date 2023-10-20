package hankyu.board.spring_board.entity.member;

import hankyu.board.spring_board.dto.member.MemberDto;
import hankyu.board.spring_board.dto.member.MemberUpdateRequest;
import hankyu.board.spring_board.entity.common.BaseTimeEntity;
import hankyu.board.spring_board.event.sign.SignUpEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    private String password;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(nullable = false)
    private boolean enabled;

    public Member(String email, String password, String username, String nickname) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.memberRole = MemberRole.ROLE_NORMAL;
        this.enabled = false;
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
                new SignUpEvent(MemberDto.toDto(this))
        );
    }

    public void confirmEmail() {
        this.enabled = true;
    }
}
