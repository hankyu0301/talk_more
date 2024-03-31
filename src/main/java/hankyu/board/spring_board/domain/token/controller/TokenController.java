package hankyu.board.spring_board.domain.token.controller;

import hankyu.board.spring_board.global.auth.jwt.JwtTokenizer;
import hankyu.board.spring_board.global.auth.utils.AccessTokenRenewalUtil;
import hankyu.board.spring_board.global.auth.utils.Token;
import hankyu.board.spring_board.global.dto.response.Response;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.token.RefreshTokenNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Api(value = "토큰 관련 API", tags = "Token")
@RequiredArgsConstructor
@RestController
public class TokenController {

    private final JwtTokenizer jwtTokenizer;
    private final AccessTokenRenewalUtil accessTokenRenewalUtil;

    @ApiOperation(value = "토큰 재발급", notes = "토큰을 재발급한다.")
    @GetMapping("/api/tokens")
    public Response getToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Token token = accessTokenRenewalUtil.renewAccessToken(request);
            jwtTokenizer.setHeaderAccessToken(response, token.getAccessToken());
            jwtTokenizer.setHeaderRefreshToken(response, token.getRefreshToken());
            return Response.success();
        } catch (ExpiredJwtException je) {
            log.error("### 리프레쉬 토큰을 찾을 수 없음");
            jwtTokenizer.resetHeaderRefreshToken(response);
            throw new RefreshTokenNotFoundException();
        } catch (MemberNotFoundException ce) {
            log.error("### 해당 회원을 찾을 수 없음");
            jwtTokenizer.resetHeaderRefreshToken(response);
            throw new MemberNotFoundException();
        }
    }
}
