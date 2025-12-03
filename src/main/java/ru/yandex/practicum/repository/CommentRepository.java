package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select count(*) from Comment c where c.post.id = :id")
    Long postCommentsCount(@Param("id") long testId);

    Optional<Comment> findByIdAndPostId(Long id, Long postId);
    List<Comment> findByPostId(Long postId);
}
