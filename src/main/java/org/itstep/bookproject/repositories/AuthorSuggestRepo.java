package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Author;
import org.itstep.bookproject.entities.AuthorSuggest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorSuggestRepo extends JpaRepository<AuthorSuggest, Long> {
    AuthorSuggest findAuthorSuggestById(Long id);
}
