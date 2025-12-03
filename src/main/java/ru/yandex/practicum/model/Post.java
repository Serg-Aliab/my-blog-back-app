package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "posts")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private Long likes;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> tags;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images;

    public Post(Long id, String title, String text, List<String> strTags) {
        this.likes = 0L;
        this.fillPost(id, title, text, strTags);
    }

    public Post fillPost(Long id, String title, String text, List<String> strTags) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = strTags.stream()
                .distinct().map(x-> new Tag(this, x))
                .collect(Collectors.toSet());
        return this;
    }

    public Post incrementLike() {
        likes += 1;
        return this;
    }
}
