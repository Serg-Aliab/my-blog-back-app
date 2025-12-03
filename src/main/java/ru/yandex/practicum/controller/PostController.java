package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.PostDto.AddPostDto;
import ru.yandex.practicum.dto.PostDto.ResponsePostDto;
import ru.yandex.practicum.dto.PostDto.ResponsePostsPageDto;
import ru.yandex.practicum.dto.PostDto.UpdatePostDto;
import ru.yandex.practicum.service.PostService;
import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePostDto add(@Valid @RequestBody AddPostDto addPostDto) {
        return postService.add(addPostDto);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePostDto get(@Valid @PathVariable(name = "id") Long id) {
        return postService.get(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponsePostsPageDto getAllSearchedByPages(
            @Valid @RequestParam String search,
            @Valid @RequestParam String pageNumber,
            @Valid @RequestParam String pageSize) {
        String encSearch = URLDecoder.decode(search, StandardCharsets.UTF_8);
        return postService.getAllSearchedByPages(encSearch,
                Integer.parseInt(pageNumber)-1, Integer.parseInt(pageSize));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponsePostDto update(@Valid @PathVariable(name = "id") Long id, @Valid @RequestBody UpdatePostDto updPostDto) {
        return postService.update(id, updPostDto);
    }

    @PostMapping(path = "/{id}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePostDto addLike(@Valid @PathVariable(name = "id") Long id) {
        return postService.addLike(id);
    }

    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @Valid @PathVariable("id") Long id,
            @RequestParam("image") MultipartFile image) throws Exception {
        if (image.isEmpty()) { return ResponseEntity.badRequest().body("empty image"); }
        boolean ok = postService.saveImage(id, image.getBytes());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to update image");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@Valid @PathVariable("id") Long id) {
        byte[] bytes = postService.getImage(id);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(bytes);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Valid @PathVariable("id") Long id) {
        postService.delete(id);
    }
}
