package hankyu.board.spring_board.dto.post;

import hankyu.board.spring_board.entity.post.Image;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImageUpdateResult {

    private List<Image> addedImages;
    private List<Image> deletedImages;
}
