package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Tag;
import org.itstep.bookproject.entities.TagSuggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagSuggestRepo extends JpaRepository<TagSuggest, Long> {
    TagSuggest findTagById(Long id);
}
