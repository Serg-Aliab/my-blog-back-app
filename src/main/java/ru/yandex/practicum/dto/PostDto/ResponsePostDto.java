package ru.yandex.practicum.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;

import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponsePostDto {
    private long id;
    private String title;
    private String text;
    private List<String> tags;
    private long likesCount;
    private long commentsCount;

    public ResponsePostDto toDto(Post post, Long commentsCount) {
        setId(post.getId());
        setTitle(post.getTitle());
        setText(post.getText());
        setTags(post.getTags().stream().map(Tag::getName)
                .sorted(Comparator.comparing(String::toLowerCase)).toList());
        setLikesCount(post.getLikes());
        setCommentsCount(commentsCount);
        return this;
    }
}
