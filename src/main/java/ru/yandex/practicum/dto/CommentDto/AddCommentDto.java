package ru.yandex.practicum.dto.CommentDto;

import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

public class AddCommentDto extends CommentDto {
    @Override
    public Comment toModel(Comment nullComment, Post post) {
        return new Comment(null, getText(), post);
    }
}
