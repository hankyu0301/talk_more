package hankyu.board.spring_board.domain.message.controller;

import hankyu.board.spring_board.domain.message.dto.MessageCreateRequest;
import hankyu.board.spring_board.domain.message.dto.MessageDeleteRequest;
import hankyu.board.spring_board.domain.message.dto.MessageReadCondition;
import hankyu.board.spring_board.domain.message.service.MessageService;
import hankyu.board.spring_board.global.dto.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Message Controller", tags = "Message")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @ApiOperation(value = "보낸 쪽지 목록 조회", notes = "보낸 쪽지 목록을 조회한다.")
    @GetMapping("/api/messages/sender")
    @ResponseStatus(HttpStatus.OK)
    public Response readAllBySender(@Valid MessageReadCondition cond) {
        return Response.success(messageService.readAllSentMessageByCond(cond));
    }

    @ApiOperation(value = "받은 쪽지 목록 조회", notes = "받은 쪽지 목록을 조회한다.")
    @GetMapping("/api/messages/receiver")
    @ResponseStatus(HttpStatus.OK)
    public Response readAllByReceiver(@Valid MessageReadCondition cond) {
        return Response.success(messageService.readAllReceivedMessageByCond(cond));
    }

    @ApiOperation(value = "쪽지 조회", notes = "쪽지를 조회한다.")
    @GetMapping("/api/messages/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@ApiParam(value = "쪽지 id", required = true) @PathVariable Long id) {
        return Response.success(messageService.read(id));
    }

    @ApiOperation(value = "쪽지 생성", notes = "쪽지를 생성한다.")
    @PostMapping("/api/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@Valid @RequestBody MessageCreateRequest req) {
        messageService.create(req);
        return Response.success();
    }

    @ApiOperation(value = "보낸 쪽지 삭제", notes = "보낸 쪽지를 삭제한다.")
    @DeleteMapping("/api/messages/sender")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteBySender(@Valid @RequestBody MessageDeleteRequest req) {
        messageService.deleteBySender(req);
        return Response.success();
    }

    @ApiOperation(value = "받은 쪽지 삭제", notes = "받은 쪽지를 삭제한다.")
    @DeleteMapping("/api/messages/receiver")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteByReceiver(@Valid @RequestBody MessageDeleteRequest req) {
        messageService.deleteByReceiver(req);
        return Response.success();
    }
}