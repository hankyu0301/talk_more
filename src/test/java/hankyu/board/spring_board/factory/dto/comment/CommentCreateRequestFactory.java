package hankyu.board.spring_board.factory.dto.comment;


import hankyu.board.spring_board.dto.comment.CommentCreateRequest;

public class CommentCreateRequestFactory {
    public static CommentCreateRequest createCommentCreateRequest() {
        return new CommentCreateRequest("content", 1L, null);
    }

    public static CommentCreateRequest createCommentCreateRequest(String content, Long postId, Long parentId) {
        return new CommentCreateRequest(content, postId, parentId);
    }

    public static CommentCreateRequest createCommentCreateRequestWithContent(String content) {
        return new CommentCreateRequest(content, 1L, null);
    }

    public static CommentCreateRequest createCommentCreateRequestWithPostId(Long postId) {
        return new CommentCreateRequest("content", postId, null);
    }

    public static CommentCreateRequest createCommentCreateRequestWithParentId(Long parentId) {
        return new CommentCreateRequest("content", 1L, parentId);
    }
}
