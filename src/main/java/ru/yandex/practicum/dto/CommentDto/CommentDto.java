package ru.yandex.practicum.dto.CommentDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.ControllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import javax.validation.constraints.NotBlank;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    @NotBlank
    private Long postId;

    public CommentDto validate(Long pathPostId) {
        if (getPostId() == null || getPostId() <= 0) {
            throw new NoIntegrityInputData("Идентификатор поста должен быть не пустым и положительным");
        }
        if (!getPostId().equals(pathPostId)) {
            throw new NoIntegrityInputData("Идентификаторы поста в строке запроса и в теле запроса не совпадают");
        }
        if (getText() == null || getText().isEmpty()) {
            throw new NoIntegrityInputData("Поле Text не может быть пустым");
        }
        return this;
    }
    public abstract Comment toModel(Comment comment, Post post);
}
