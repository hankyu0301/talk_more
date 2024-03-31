package hankyu.board.spring_board.domain.mail.controller;


import hankyu.board.spring_board.domain.mail.dto.EmailConfirmRequest;
import hankyu.board.spring_board.domain.mail.dto.ResendEmailRequest;
import hankyu.board.spring_board.domain.mail.service.EmailService;
import hankyu.board.spring_board.global.dto.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static hankyu.board.spring_board.global.dto.response.Response.success;

@Api(value = "이메일 관련 API", tags = "Email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @ApiOperation(value = "이메일 인증", notes = "이메일 인증을 한다.")
    @GetMapping("/api/email")
    @ResponseStatus(HttpStatus.OK)
    public Response confirmEmail(@ModelAttribute EmailConfirmRequest req) {
        emailService.confirmEmail(req);
        return success();
    }

    @ApiOperation(value = "인증 메일 재발송", notes = "인증 메일을 재발송 한다.")
    @PostMapping("/api/email")
    @ResponseStatus(HttpStatus.OK)
    public Response resendEmail(@Valid @RequestBody ResendEmailRequest req) {
        emailService.resend(req);
        return success();
    }

}
