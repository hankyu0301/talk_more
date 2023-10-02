package hankyu.board.spring_board.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import hankyu.board.spring_board.dto.member.MemberDto;
import hankyu.board.spring_board.entity.comment.Comment;
import hankyu.board.spring_board.helper.HierarchyConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private MemberDto member;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<CommentDto> children;
    //category -> category.getParent() == null ? null : category.getParent().getId(),

    public static List<CommentDto> toDtoList(List<Comment> comments) {
        HierarchyConverter<Comment, CommentDto> converter = HierarchyConverter.create(
                comments,
                c -> new CommentDto(c.getId(), c.isDeleted() ? null : c.getContent(), c.isDeleted() ? null : MemberDto.toDto(c.getMember()), c.getCreatedAt(), new ArrayList<>()),
                c -> c.getParent() == null ? null : c.getParent().getId(),
                Comment::getId,
                CommentDto::getChildren);
        return converter.convertToHierarchy();
    }
}