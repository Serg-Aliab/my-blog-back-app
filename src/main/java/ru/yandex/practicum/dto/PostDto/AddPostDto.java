package ru.yandex.practicum.dto.PostDto;

import ru.yandex.practicum.model.Post;

public class AddPostDto extends PostDto {
    @Override
    public Post toModel(Post nullPost) {
        return new Post(null, getTitle(), getText(), getTags());
    }
}
