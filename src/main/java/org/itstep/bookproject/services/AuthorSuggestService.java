package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Author;
import org.itstep.bookproject.entities.AuthorSuggest;
import org.itstep.bookproject.entities.TagSuggest;

import java.util.List;

public interface AuthorSuggestService {
    AuthorSuggest updateAuthorSuggest(AuthorSuggest authorSuggest);
    void deleteAuthorSuggest(Long id);
    List<AuthorSuggest> getAuthorsSuggestions();
    AuthorSuggest getAuthorSuggestions(Long id);
}
