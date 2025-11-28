package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ControllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.dto.CommentDto.AddCommentDto;
import ru.yandex.practicum.dto.CommentDto.ResponseCommentDto;
import ru.yandex.practicum.dto.CommentDto.UpdateCommentDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public ResponseCommentDto add(Long pathPostId, AddCommentDto commentDto) {
        Post post = postRepository.findById(pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + pathPostId + " не найден"));
        Comment savedComment = commentRepository.save(commentDto.validate(pathPostId).toModel(null, post));
        return new ResponseCommentDto().toDto(savedComment);
    }

    public ResponseCommentDto get(Long pathPostId, Long commentId) {
        Post post = postRepository.findById(pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + pathPostId + " не найден"));
        Comment comment = commentRepository.findByIdAndPostId(commentId, pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Комментарий с идентификатором " + commentId + " не найден"));
        return new ResponseCommentDto().toDto(comment);
    }

    public List<ResponseCommentDto> getAllForPost(Long pathPostId) {
        List<Comment> comments = commentRepository.findByPostId(pathPostId);
        return comments.stream().map(x->new ResponseCommentDto().toDto(x)).toList();
    }

    @Transactional
    public ResponseCommentDto update(Long pathPostId, Long commentId, UpdateCommentDto commentDto) {
        Post post = postRepository.findById(pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + pathPostId + " не найден"));
        Comment comment = commentRepository.findByIdAndPostId(commentId, pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Комментарий с идентификатором " + commentId + " не найден"));
        Comment savedComment = commentRepository.save(commentDto.validate(pathPostId, commentId).toModel(comment, post));
        return new ResponseCommentDto().toDto(savedComment);
    }

    @Transactional
    public void delete(Long pathPostId, Long commentId) {
        Post post = postRepository.findById(pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + pathPostId + " не найден"));
        Comment comment = commentRepository.findByIdAndPostId(commentId, pathPostId).orElseThrow(()->
                new NoIntegrityInputData("Комментарий с идентификатором " + commentId + " не найден"));
        commentRepository.delete(comment);
    }
}
