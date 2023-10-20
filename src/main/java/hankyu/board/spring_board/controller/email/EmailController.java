package hankyu.board.spring_board.controller.email;


import hankyu.board.spring_board.dto.email.EmailConfirmRequest;
import hankyu.board.spring_board.dto.email.ResendEmailRequest;
import hankyu.board.spring_board.dto.response.Response;
import hankyu.board.spring_board.service.email.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static hankyu.board.spring_board.dto.response.Response.success;

@Api(value = "Email Controller", tags = "Email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @ApiOperation(value = "이메일 인증", notes = "이메일 인증을 한다.")
    @GetMapping("/api/confirm-email")
    @ResponseStatus(HttpStatus.OK)
    public Response confirmEmail(@ModelAttribute EmailConfirmRequest req) {
        emailService.confirmEmail(req);
        return success();
    }

    @ApiOperation(value = "인증 메일 재발송", notes = "인증 메일을 재발송 한다.")
    @PostMapping("/api/resend-email")
    @ResponseStatus(HttpStatus.OK)
    public Response resendEmail(@Valid @RequestBody ResendEmailRequest req) {
        emailService.resend(req);
        return success();
    }

}
