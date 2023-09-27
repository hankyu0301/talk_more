package hankyu.board.spring_board.factory.dto.post;

import hankyu.board.spring_board.dto.post.PostUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PostUpdateRequestFactory {
    public static PostUpdateRequest createPostUpdateRequest(String title, String content, List<MultipartFile> addedImages, List<Long> deletedImages) {
        return new PostUpdateRequest(title, content, addedImages, deletedImages);
    }
}
