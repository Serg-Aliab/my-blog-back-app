package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ControllerExceptionHandler.Exceptions.NoIntegrityInputData;
import ru.yandex.practicum.dto.PostDto.AddPostDto;
import ru.yandex.practicum.dto.PostDto.ResponsePostDto;
import ru.yandex.practicum.dto.PostDto.ResponsePostsPageDto;
import ru.yandex.practicum.dto.PostDto.UpdatePostDto;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Transactional
    public ResponsePostDto add(AddPostDto postDto) {
        Post savedPost = postRepository.save(postDto.validate().toModel(null));
        return new ResponsePostDto().toDto(savedPost, 0L);
    }

    public ResponsePostDto get(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + id + " не найден"));
        Long commentsCount = commentRepository.postCommentsCount(post.getId());
        return new ResponsePostDto().toDto(post, commentsCount);
    }

    public ResponsePostsPageDto getAllSearchedByPages(String search, int pageNumber, int pageSize) {
        Long querySize;
        List<Post> queryPage;
        Pageable reqvPage = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        Pair<String, List<String>> searchParse = parseSearchString(search);
        if (!searchParse.a.isEmpty() && !searchParse.b.isEmpty()) {
            querySize = postRepository.countDistinctPostByTags_NameInAndTitleContains(
                    searchParse.b, searchParse.a);
            queryPage = postRepository.findDistinctPostByTags_NameInAndTitleContains(
                                        searchParse.b, searchParse.a, reqvPage);
        } else if (!searchParse.a.isEmpty()) {
            querySize = postRepository.countByTitleContains(searchParse.a);
            queryPage = postRepository.findByTitleContains(searchParse.a, reqvPage);

        } else if (!searchParse.b.isEmpty()) {
            querySize = postRepository.countDistinctPostByTags_NameIn(searchParse.b);
            queryPage = postRepository.findDistinctPostByTags_NameIn(searchParse.b, reqvPage);
        } else {
            querySize = postRepository.count();
            queryPage = postRepository.findAll(reqvPage).toList();
        }
        return new ResponsePostsPageDto().toDto(commentRepository,
                queryPage, pageNumber, pageSize, querySize);
    }

    public Pair<String, List<String>> parseSearchString(String search) {
        String subForTitle = "";
        List<String> tags = new ArrayList<>();
        // 1. Строка поиска разбивается на слова по пробелам.
        String[] strArray = search.split("\\s+");
        for (String str: strArray) {
            // 2. Пустые слова удаляются из поиска.
            if (!str.isEmpty()) {
                if (str.startsWith("#")) {
                    // 3. Слова, начинающиеся с #, считаются тегами, и посты фильтруются по ним по «И».
                    tags.add(str.substring(1));
                } else {
                    // 4. Слова, не начинающиеся с #, склеиваются вместе через пробел и считаются подстрокой поиска по названию.
                    subForTitle = subForTitle + " " + str;
                }
            }
        }
        return new Pair<>(subForTitle.trim(), tags);
    }

    @Transactional
    public ResponsePostDto update(Long id, UpdatePostDto postDto) {
        Post post = postRepository.findById(id).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + id + " не найден"));
        Post savedPost = postRepository.save(postDto.validate(id).toModel(post));
        Long commentsCount = commentRepository.postCommentsCount(id);
        return new ResponsePostDto().toDto(savedPost, commentsCount);
    }

    @Transactional
    public ResponsePostDto addLike(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + id + " не найден"));
        postRepository.save(post.incrementLike());
        Long commentsCount = commentRepository.postCommentsCount(id);
        return new ResponsePostDto().toDto(post, commentsCount);
    }

    @Transactional
    public boolean saveImage(Long id, byte[] imageData) {
        Image image;
        List<Image> images = imageRepository.findByPostId(id);
        if(images == null || images.isEmpty()) {
            Post post = postRepository.findById(id).orElseThrow(()->
                    new NoIntegrityInputData("Пост с идентификатором " + id + " не найден"));
            image = new Image(null, imageData, post);
        } else {
            image = images.getFirst();
            image.setData(imageData);
        }
        Image savedImage = imageRepository.save(image);
        return savedImage.getId() > 0;
    }

    @Transactional
    public byte[] getImage(Long id) {
        List<Image> images = imageRepository.findByPostId(id);
        if (images != null && !images.isEmpty()) {
            return images.getFirst().getData();
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->
                new NoIntegrityInputData("Пост с идентификатором " + id + " не найден"));
        postRepository.delete(post);
    }
}
