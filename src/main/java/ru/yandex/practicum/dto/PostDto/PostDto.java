package ru.yandex.practicum.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.controllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.model.Post;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class PostDto {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String text;
    @NotBlank
    private List<String> tags;

    public PostDto validate() {
        if (getTitle() == null || getTitle().isEmpty()) {
            throw new NoIntegrityInputData("Поле Title не может быть пустым");
        }
        if (getText() == null || getText().isEmpty()) {
            throw new NoIntegrityInputData("Поле Text не может быть пустым");
        }
        if (getTags() == null || getTags().isEmpty()) {
            throw new NoIntegrityInputData("Поле Tag не может быть пустым");
        }
        return this;
    }
    public abstract Post toModel(Post post);
}
