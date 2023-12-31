package hankyu.board.spring_board.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hankyu.board.spring_board.domain.member.dto.MemberDto;
import hankyu.board.spring_board.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String content;

    private MemberDto writer;

    private List<ImageDto> images;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public static PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                MemberDto.toDto(post.getMember()),
                post.getImages().stream().map(ImageDto::toDto).collect(Collectors.toList()),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
