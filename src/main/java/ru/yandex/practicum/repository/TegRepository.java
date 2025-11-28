package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Tag;

@Repository
public interface TegRepository extends JpaRepository<Tag, Long> {
}
