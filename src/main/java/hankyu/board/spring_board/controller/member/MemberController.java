package hankyu.board.spring_board.controller.member;

import hankyu.board.spring_board.dto.member.MemberDeleteRequest;
import hankyu.board.spring_board.dto.member.MemberUpdateRequest;
import hankyu.board.spring_board.dto.response.Response;
import hankyu.board.spring_board.service.member.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static hankyu.board.spring_board.dto.response.Response.success;

@Api(value = "Member Controller", tags = "Member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "회원 조회", notes = "회원 조회를 한다.")
    @GetMapping("/api/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response findMember(@ApiParam(value = "회원 id", required = true)@PathVariable Long id) {
        return success(memberService.findMember(id));
    }

    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보 수정을 한다.")
    @PutMapping("/api/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(@ApiParam(value = "회원 id", required = true) @PathVariable Long id, @Valid @RequestBody MemberUpdateRequest req) {
        memberService.update(id, req);
        return success();
    }

    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴를 한다.")
    @DeleteMapping("/api/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "회원 id", required = true) @PathVariable Long id, @Valid @RequestBody MemberDeleteRequest req) {
        memberService.delete(id, req);
        return success();
    }

}
