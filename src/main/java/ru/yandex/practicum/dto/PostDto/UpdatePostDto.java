package ru.yandex.practicum.dto.PostDto;

import ru.yandex.practicum.controllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.model.Post;

public class UpdatePostDto extends PostDto {
    public PostDto validate(Long id) {
        super.validate();
        if (getId() == null || getId() <= 0) {
            throw new NoIntegrityInputData("Идентификатор поста должен быть не пустым и положительным");
        }
        if (!getId().equals(id)) {
            throw new NoIntegrityInputData("Идентификаторы поста в строке запроса и в теле запроса не совпадают");
        }
        return this;
    }

    @Override
    public Post toModel(Post post) {
        return post.fillPost(getId(), getTitle(), getText(), getTags());
    }
}
