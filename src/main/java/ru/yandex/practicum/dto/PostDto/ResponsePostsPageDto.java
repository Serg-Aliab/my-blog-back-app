package ru.yandex.practicum.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponsePostsPageDto {
    private List<ResponsePostDto> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

    public ResponsePostsPageDto toDto(
            CommentRepository commentRepository, List<Post> postsPage,
            int pageNumber, int pageSize, long querySize) {
        this.posts = postsPage.stream().map(x->new ResponsePostDto()
                        .toDto(x, commentRepository.postCommentsCount(x.getId()))).toList();
        this.lastPage = (int)(querySize/pageSize);
        if (querySize%pageSize > 0) { this.lastPage += 1; }
        this.hasPrev = !(pageNumber == 0);
        this.hasNext = !(pageNumber == this.lastPage-1);
        return this;
    }
}
