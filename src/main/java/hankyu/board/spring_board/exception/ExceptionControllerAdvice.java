package hankyu.board.spring_board.exception;

import hankyu.board.spring_board.dto.response.Response;
import hankyu.board.spring_board.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.exception.comment.CommentNotFoundException;
import hankyu.board.spring_board.exception.common.CannotConvertNestedStructureException;
import hankyu.board.spring_board.exception.common.UnauthorizedAccessException;
import hankyu.board.spring_board.exception.email.EmailAlreadyVerifiedException;
import hankyu.board.spring_board.exception.email.InvalidVerificationCodeException;
import hankyu.board.spring_board.exception.file.FileUploadFailureException;
import hankyu.board.spring_board.exception.member.DuplicateEmailException;
import hankyu.board.spring_board.exception.member.DuplicateNicknameException;
import hankyu.board.spring_board.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.exception.post.ImageNotFoundException;
import hankyu.board.spring_board.exception.post.PostNotFoundException;
import hankyu.board.spring_board.exception.post.UnsupportedImageFormatException;
import hankyu.board.spring_board.exception.sign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    // 500 에러
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        log.error("e = {}", e.getMessage());
        log.error("Exception occurred: {}", e.getClass().getSimpleName());
        return Response.failure(500, e.getMessage());
    }

    // 500 응답
    @ExceptionHandler(CannotConvertNestedStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response cannotConvertNestedStructureException() {
        return Response.failure(500, "계층 구조로 변환할 수 없습니다.");
    }

    // 500 응답
    @ExceptionHandler(FileUploadFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response fileUploadFailureException() {
        return Response.failure(500, "파일 업로드에 실패했습니다.");
    }

    // 400 에러
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(BindException e) {
        return Response.failure(400, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    // 400 에러
    @ExceptionHandler(UnsupportedImageFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response unsupportedImageFormatException() {
        return Response.failure(400, "지원하지 않는 이미지 포맷입니다.");
    }

    // 400 에러
    @ExceptionHandler(TokenInvalidSecretKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response tokenInvalidSecretKeyException() {
        return Response.failure(400, "유효하지 않은 토큰 비밀키 입니다.");
    }

    // 400 에러
    @ExceptionHandler(MalformedJwtTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response malformedJwtTokenException() {
        return Response.failure(400, "유효하지 않은 토큰 값 입니다.");
    }

    // 400 에러
    @ExceptionHandler(InvalidVerificationCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response invalidVerficationCodeException() {
        return Response.failure(400, "유효하지 않은 인증코드 입니다.");
    }

    // 401 응답
    // 아이디 혹은 비밀번호 오류시 발생
    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response loginFailureException() {
        return Response.failure(401, "로그인에 실패하였습니다.");
    }

    // 401 응답
    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response expiredTokenException() {
        return Response.failure(401, "만료된 토큰입니다.");
    }


    // 401 응답
    @ExceptionHandler(AlreadyLoggedOutAccessTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response alreadyLoggedOutAccessTokenException() {
        return Response.failure(401, "이미 로그아웃 처리된 토큰입니다.");
    }

    // 401 응답
    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response invalidRefreshTokenException() {
        return Response.failure(401, "유효하지 않은 토큰입니다.");
    }

    // 403 응답
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response accessDeniedException(){
        return Response.failure(403, "접근이 거부 되었습니다.");
    }

    // 403 응답
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response unauthorizedAccessException(){
        return Response.failure(403, "접근이 거부 되었습니다.");
    }

    // 404 응답
    // 요청한 카테고리를 찾을 수 없음
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException() {
        return Response.failure(404, "요청한 카테고리를 찾을 수 없습니다.");
    }

    // 404 응답
    // 요청한 댓글을 찾을 수 없음
    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response commentNotFoundException() {
        return Response.failure(404, "요청한 댓글을 찾을 수 없습니다.");
    }

    // 404 응답
    // 요청한 회원을 찾을 수 없음
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException() {
        return Response.failure(404, "요청한 회원을 찾을 수 없습니다.");
    }

    // 404 응답
    // 요청한 이미지를 찾을 수 없음
    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response imageNotFoundException() {
        return Response.failure(404, "요청한 이미지를 찾을 수 없습니다.");
    }

    // 404 응답
    // 요청한 게시글을 찾을 수 없음
    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException() {
        return Response.failure(404, "요청한 이미지를 찾을 수 없습니다.");
    }

    // 409 응답
    // username 중복
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response duplicateEmailException(DuplicateEmailException e) {
        return Response.failure(409, e.getMessage() + "은 중복된 아이디 입니다.");
    }

    // 409 응답
    // nickname 중복
    @ExceptionHandler(DuplicateNicknameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response duplicateNicknameException(DuplicateNicknameException e) {
        return Response.failure(409, e.getMessage() + "은 중복된 닉네임 입니다.");
    }

    // 409 응답
    // nickname 중복
    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response emailAlreadyVerifiedException() {
        return Response.failure(409,  "이미 인증된 이메일 입니다.");
    }


}
