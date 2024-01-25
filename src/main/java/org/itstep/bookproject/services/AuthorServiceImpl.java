package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Author;
import org.itstep.bookproject.entities.Tag;
import org.itstep.bookproject.repositories.AuthorRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepo authorRepo;

    public AuthorServiceImpl(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }


    @Override
    public Author updateAuthor(Author author) {
        return authorRepo.save(author);
    }

    @Override
    public List<Author> getAuthors() {
        return authorRepo.findAll();
    }

    @Override
    public Author getAuthor(Long id) {
        return authorRepo.findAuthorById(id);
    }
}
