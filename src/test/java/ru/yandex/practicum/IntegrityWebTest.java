package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.EntityManagerTestConfig;
import ru.yandex.practicum.configuration.MultipartConfiguration;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import java.util.Arrays;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
        WebConfiguration.class,
        EntityManagerTestConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class IntegrityWebTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    private MockMvc mockMvc;
    private Post inPost1;
    private Comment inComment1;
    private Post inPostLast;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

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

        inPostLast = new Post(null,
                "Название поста 02",
                "Текст поста 02",
                Arrays.asList("tag_12", "tag_21", "tag_22", "tag_23"));
        postRepository.save(inPostLast);
    }

    @Test
    @Transactional
    void t000_getPostsBySearchAndPaging_acceptsJson_andPersists() throws Exception {
        mockMvc.perform(get("/api/posts?search=поста %23tag_12&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].tags", hasSize(3)))
                .andExpect(jsonPath("$.posts[0].likesCount").value(3))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(2))
                .andExpect(jsonPath("$.posts[1].tags", hasSize(4)))
                .andExpect(jsonPath("$.posts[1].likesCount").value(0))
                .andExpect(jsonPath("$.posts[1].commentsCount").value(0))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    @Transactional
    void t00_incrementLikesInPost_acceptsJson_andPersists() throws Exception {
        Long getID = inPost1.getId();
        mockMvc.perform(post("/api/posts/{id}/likes", getID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(getID))
                .andExpect(jsonPath("$.title").value("Название поста 01"))
                .andExpect(jsonPath("$.tags.[0]").value("tag_11"))
                .andExpect(jsonPath("$.tags.[1]").value("tag_12"))
                .andExpect(jsonPath("$.tags.[2]").value("tag_13"))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.likesCount").value(4))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    @Transactional
    void t0_getPost_acceptsJson_andPersists() throws Exception {
        Long getID = inPost1.getId();
        mockMvc.perform(get("/api/posts/" + getID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(getID))
                .andExpect(jsonPath("$.title").value("Название поста 01"))
                .andExpect(jsonPath("$.tags.[0]").value("tag_11"))
                .andExpect(jsonPath("$.tags.[1]").value("tag_12"))
                .andExpect(jsonPath("$.tags.[2]").value("tag_13"))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.likesCount").value(3))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    @Transactional
    void t1_addPost_acceptsJson_andPersists() throws Exception {
        String json = """
            {
                "title": "Название поста 88",
                "text": "Текст поста в формате Markdown...",
                "tags": ["tag_2", "tag_1", "tag_2"]
            }
            """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(inPostLast.getId()+1))
                .andExpect(jsonPath("$.title").value("Название поста 88"))
                .andExpect(jsonPath("$.tags.[0]").value("tag_1"))
                .andExpect(jsonPath("$.tags.[1]").value("tag_2"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        mockMvc.perform(get("/api/posts?search=поста&pageNumber=1&pageSize=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(3)));
    }

    @Test
    @Transactional
    void t2_updatePost_acceptsJson_andPersists() throws Exception {
        Long updID = inPost1.getId();
        String json = String.format("""
            {
                "id": %d,
                "title": "Название поста 02",
                "text": "Текст поста 02",
                "tags": ["tag_77", "tag_88"]
            }
            """, updID);

        mockMvc.perform(put("/api/posts/" + updID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updID))
                .andExpect(jsonPath("$.title").value("Название поста 02"))
                .andExpect(jsonPath("$.tags.[0]").value("tag_77"))
                .andExpect(jsonPath("$.tags.[1]").value("tag_88"))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.likesCount").value(3))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    @Transactional
    void t3_deletePost_noContent() throws Exception {
        Long updID = inPostLast.getId();
        mockMvc.perform(delete("/api/posts/" + updID))
                .andExpect(status().isOk());
        assertEquals(1, postRepository.findAll().size());

        mockMvc.perform(get("/api/posts?search=поста&pageNumber=1&pageSize=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)));
    }

    @Test
    void t31_uploadAndDownloadImage_success() throws Exception {
        Long postID = inPost1.getId();
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile image = new MockMultipartFile("image", "avatar.png", "image/png", pngStub);

        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", postID).file(image))
                .andExpect(status().isCreated())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/api/posts/{id}/image", postID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    @Transactional
    void t4_getComment_acceptsJson_andPersists() throws Exception {
        Long postID = inPost1.getId();
        Long inComm1 = inComment1.getId();
        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postID, inComm1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(inComm1))
                .andExpect(jsonPath("$.postId").value(postID))
                .andExpect(jsonPath("$.text").value("Коммент 1"));
    }

    @Test
    @Transactional
    void t5_getAllCommentForPost_acceptsJson_andPersists() throws Exception {
        Long postID = inPost1.getId();
        mockMvc.perform(get("/api/posts/{id}/comments", postID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text").value("Коммент 1"))
                .andExpect(jsonPath("$[1].text").value("Коммент 2"));
    }

    @Test
    @Transactional
    void t6_addComment_acceptsJson_andPersists() throws Exception {
        Long postID = inPost1.getId();
        String json = String.format("""
            {
                "text": "Это добавленный комментарий 2",
                "postId": %d
            }
            """, postID);

        mockMvc.perform(post("/api/posts/{id}/comments", postID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postID))
                .andExpect(jsonPath("$.text").value("Это добавленный комментарий 2"));

        mockMvc.perform(get("/api/posts/{id}/comments", postID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Transactional
    void t7_updateComment_acceptsJson_andPersists() throws Exception {
        Long postID = inPost1.getId();
        Long inComm1 = inComment1.getId();
        String json = String.format("""
            {
                "id": %d,
                "text": "Это измененный комментарий 77",
                "postId": %d
            }
            """, inComm1, postID);

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postID, inComm1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(inComm1))
                .andExpect(jsonPath("$.postId").value(postID))
                .andExpect(jsonPath("$.text").value("Это измененный комментарий 77"));
    }

    @Test
    @Transactional
    void t8_deleteComment_noContent() throws Exception {
        Long postID = inPost1.getId();
        Long inComm1 = inComment1.getId();
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postID, inComm1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}/comments", postID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
