package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepo extends JpaRepository<Author, Long> {
    Author findAuthorById(Long id);
}
