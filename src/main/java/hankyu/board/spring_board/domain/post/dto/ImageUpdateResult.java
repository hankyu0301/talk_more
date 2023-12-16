package hankyu.board.spring_board.domain.post.dto;

import hankyu.board.spring_board.domain.post.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImageUpdateResult {

    private List<Image> addedImages;
    private List<Image> deletedImages;
}
