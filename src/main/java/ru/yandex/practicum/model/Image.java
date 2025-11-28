package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "images")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob @Lazy @Column(name = "image_data")
    private byte[] data;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Post post;
}
