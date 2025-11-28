package ru.yandex.practicum;

import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.configuration.EntityManagerTestConfig;
import ru.yandex.practicum.service.PostService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {
        WebConfiguration.class,
        EntityManagerTestConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostServiceTest {
    @Autowired
    PostService postService;

    @Test
    void t1_ParseSearchString_Test() {
        Pair<String, List<String>> res = postService.parseSearchString("qwe #asdf rty #ghjk");
        assertEquals("qwe rty", res.a);
        assertEquals(2, res.b.size());
        assertEquals("asdf", res.b.getFirst());
        assertEquals("ghjk", res.b.getLast());
    }
}
