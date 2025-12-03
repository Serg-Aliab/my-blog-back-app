package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Long countDistinctPostByTags_NameIn(List<String> tags);
    List<Post> findDistinctPostByTags_NameIn(List<String> tags, Pageable pageable);
    Long countByTitleContains(String titleLike);
    List<Post> findByTitleContains(String titleLike, Pageable pageable);
    Long countDistinctPostByTags_NameInAndTitleContains(
            List<String> tags, String titleLike);
    List<Post> findDistinctPostByTags_NameInAndTitleContains(
            List<String> tags, String titleLike, Pageable pageable);
}
