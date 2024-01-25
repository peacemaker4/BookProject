package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Author;

import java.util.List;

public interface AuthorService {
    Author updateAuthor(Author author);
    List<Author> getAuthors();
    Author getAuthor(Long id);
}
