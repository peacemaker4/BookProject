package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepo extends JpaRepository<Tag, Long> {
    Tag findTagById(Long id);
}
