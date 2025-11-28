package ru.yandex.practicum.dto.CommentDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;

import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseCommentDto {
    private long id;
    private String text;
    private Long postId;

    public ResponseCommentDto toDto(Comment comment) {
        setId(comment.getId());
        setText(comment.getText());
        setPostId(comment.getPost().getId());
        return this;
    }
}
