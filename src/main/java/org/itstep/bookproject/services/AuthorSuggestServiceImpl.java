package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.AuthorSuggest;
import org.itstep.bookproject.repositories.AuthorSuggestRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class AuthorSuggestServiceImpl implements AuthorSuggestService {

    private final AuthorSuggestRepo authorSuggestRepo;

    public AuthorSuggestServiceImpl(AuthorSuggestRepo authorSuggestRepo) {
        this.authorSuggestRepo = authorSuggestRepo;
    }

    @Override
    public AuthorSuggest updateAuthorSuggest(AuthorSuggest authorSuggest) {
        return authorSuggestRepo.save(authorSuggest);
    }

    @Override
    public void deleteAuthorSuggest(Long id) {
        authorSuggestRepo.deleteById(id);
    }

    @Override
    public List<AuthorSuggest> getAuthorsSuggestions() {
        return authorSuggestRepo.findAll();
    }

    @Override
    public AuthorSuggest getAuthorSuggestions(Long id) {
        return authorSuggestRepo.findAuthorSuggestById(id);
    }
}
