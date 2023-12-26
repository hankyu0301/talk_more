package hankyu.board.spring_board.domain.oauth.entity;

import hankyu.board.spring_board.domain.common.BaseTimeEntity;
import hankyu.board.spring_board.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class KakaoToken extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column
    private String accessToken;
    @Column
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
