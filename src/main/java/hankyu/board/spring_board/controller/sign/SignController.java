package hankyu.board.spring_board.controller.sign;

import hankyu.board.spring_board.dto.response.Response;
import hankyu.board.spring_board.dto.sign.LogoutRequest;
import hankyu.board.spring_board.dto.sign.SignInRequest;
import hankyu.board.spring_board.dto.sign.SignUpRequest;
import hankyu.board.spring_board.dto.token.TokenReissueRequest;
import hankyu.board.spring_board.service.sign.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static hankyu.board.spring_board.dto.response.Response.success;

@Api(value = "Sign Controller", tags = "Sign")
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @ApiOperation(value = "회원 가입", notes = "회원 가입 한다.")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req) {
        signService.signUp(req);
        return success();
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest req) {
        return success(signService.signIn(req));
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃을 한다.")
    @PostMapping("/api/log-out")
    @ResponseStatus(HttpStatus.OK)
    public Response logout(@Valid @RequestBody LogoutRequest req) {
        signService.logout(req);
        return success();
    }

    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급을 한다.")
    @PostMapping("/api/token")
    @ResponseStatus(HttpStatus.OK)
    public Response reissue(@RequestBody TokenReissueRequest req) {
        return success(signService.reissue(req));
    }

}
