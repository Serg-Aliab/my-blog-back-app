package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.configuration.EntityManagerTestConfig;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {
        WebConfiguration.class,
        EntityManagerTestConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class DaoTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Post inPost1;
    private Comment inComment1;
    private Post inPostLast;

    @BeforeEach
    void setup() {
        // Чистим и наполняем БД перед каждым тестом
        postRepository.deleteAll();

        inPost1 = new Post(null,
                "Название поста 01",
                "Текст поста 01",
                Arrays.asList("tag_11", "tag_12", "tag_13"));
        inPost1.incrementLike();
        inPost1.incrementLike();
        inPost1.incrementLike();
        postRepository.save(inPost1);
        inComment1 = new Comment(null, "Коммент 1", inPost1);
        commentRepository.save(inComment1);
        Comment inComment2 = new Comment(null, "Коммент 2", inPost1);
        commentRepository.save(inComment2);

        Post inPost2 = new Post(null,
                "Название новое 03",
                "Текст новое 03",
                Arrays.asList("tag_77", "tag_88", "tag_99"));
        postRepository.save(inPost2);

        inPostLast = new Post(null,
                "Название поста 02",
                "Текст поста 02",
                Arrays.asList("tag_21", "tag_22", "tag_23"));
        postRepository.save(inPostLast);
    }

    @Test
    void t1_findDistinctPostByTags_NameIn_test() {
        Long count = postRepository.countDistinctPostByTags_NameIn(List.of("tag_12", "tag_21"));
        assertEquals(2, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findDistinctPostByTags_NameIn(List.of("tag_12", "tag_21"), reqvPage);
        assertEquals(2, posts.size());
    }

    @Test
    void t2_findDistinctPostByTags_NameIn_test() {
        Long count = postRepository.countDistinctPostByTags_NameIn(List.of("tag_12"));
        assertEquals(1, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findDistinctPostByTags_NameIn(List.of("tag_12"), reqvPage);
        assertEquals(1, posts.size());
    }

    @Test
    void t3_findByTitleContains_test() {
        Long count = postRepository.countByTitleContains("поста");
        assertEquals(2, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findByTitleContains("поста", reqvPage);
        assertEquals(2, posts.size());
    }

    @Test
    void t4_findByTitleContains_test() {
        Long count = postRepository.countByTitleContains(" 01");
        assertEquals(1, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findByTitleContains("01", reqvPage);
        assertEquals(1, posts.size());
    }

    @Test void t5_findDistinctPostByTags_NameInAndTitleContains_test() {
        Long count = postRepository.countDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21"), "qwerty");
        assertEquals(0, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21"), "qwerty", reqvPage);
        assertEquals(0, posts.size());
    }

    @Test void t6_findDistinctPostByTags_NameInAndTitleContains_test() {
        Long count = postRepository.countDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21"), "новое");
        assertEquals(0, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21"), "новое", reqvPage);
        assertEquals(0, posts.size());
    }

    @Test void t7_findDistinctPostByTags_NameInAndTitleContains_test() {
        Long count = postRepository.countDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21", "tag_88"), "новое");
        assertEquals(1, count);

        Pageable reqvPage = PageRequest.of(0, 100, Sort.by("id"));
        List<Post> posts = postRepository.findDistinctPostByTags_NameInAndTitleContains(
                List.of("tag_12", "tag_21", "tag_88"), "новое", reqvPage);
        assertEquals(1, posts.size());
    }
}
