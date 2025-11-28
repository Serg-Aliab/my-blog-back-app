package ru.yandex.practicum.dto.CommentDto;

import org.antlr.v4.runtime.misc.Pair;
import ru.yandex.practicum.ControllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

public class UpdateCommentDto extends CommentDto {
    public CommentDto validate(Long pathPostId, Long commentId) {
        super.validate(pathPostId);
        if (getId() == null || getId() <= 0) {
            throw new NoIntegrityInputData("Идентификатор комментария должен быть не пустым и положительным");
        }
        if (!getId().equals(commentId)) {
            throw new NoIntegrityInputData("Идентификаторы комментария в строке запроса и в теле запроса не совпадают");
        }
        return this;
    }

    @Override
    public Comment toModel(Comment comment, Post post) {
        return comment.fillComment(getId(), getText(), post);
    }
}
