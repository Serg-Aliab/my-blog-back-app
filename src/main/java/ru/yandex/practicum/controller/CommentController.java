package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentDto.AddCommentDto;
import ru.yandex.practicum.dto.CommentDto.ResponseCommentDto;
import ru.yandex.practicum.dto.CommentDto.UpdateCommentDto;
import ru.yandex.practicum.service.CommentService;
import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/posts")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping(path = "/{postId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDto add(@Valid @PathVariable(name = "postId") Long postId,
                                  @Valid @RequestBody AddCommentDto addCommentDto) {
        return commentService.add(postId, addCommentDto);
    }

    @GetMapping(path = "/{postId}/comments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseCommentDto get(@Valid @PathVariable(name = "postId") Long postId,
                                  @Valid @PathVariable(name = "id") Long id) {
        return commentService.get(postId, id);
    }

    @GetMapping(path = "/{postId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseCommentDto> getAllForPost(@Valid @PathVariable(name = "postId") Long postId) {
        return commentService.getAllForPost(postId);
    }

    @PutMapping(path = "/{postId}/comments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseCommentDto update(@Valid @PathVariable(name = "postId") Long postId,
                                     @Valid @PathVariable(name = "id") Long id,
                                     @Valid @RequestBody UpdateCommentDto updCommentDto) {
        return commentService.update(postId, id, updCommentDto);
    }

    @DeleteMapping("/{postId}/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Valid @PathVariable(name = "postId") Long postId,
                       @Valid @PathVariable("id") Long id) {
        commentService.delete(postId, id);
    }
}
