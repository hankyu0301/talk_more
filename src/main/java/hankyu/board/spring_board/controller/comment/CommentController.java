package hankyu.board.spring_board.controller.comment;

import hankyu.board.spring_board.dto.comment.CommentCreateRequest;
import hankyu.board.spring_board.dto.comment.CommentReadCondition;
import hankyu.board.spring_board.dto.response.Response;
import hankyu.board.spring_board.service.comment.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Comment Controller", tags = "Comment")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록 조회", notes = "댓글 목록을 조회한다.")
    @GetMapping("/api/comments")
    @ResponseStatus(HttpStatus.OK)
    public Response readAll(@Valid CommentReadCondition cond){
        return Response.success(commentService.readAll(cond));
    }

    @ApiOperation(value = "댓글 생성", notes = "댓글을 생성한다.")
    @PostMapping("/api/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@Valid @RequestBody CommentCreateRequest request){
        commentService.create(request);
        return Response.success();
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.")
    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@PathVariable Long id){
        commentService.delete(id);
        return Response.success();
    }
}
