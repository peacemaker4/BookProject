package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.TagSuggest;
import org.itstep.bookproject.repositories.TagSuggestRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class TagSuggestServiceImpl implements TagSuggestService {

    private final TagSuggestRepo tagSuggestRepo;

    public TagSuggestServiceImpl(TagSuggestRepo tagSuggestRepo) {
        this.tagSuggestRepo = tagSuggestRepo;
    }

    @Override
    public TagSuggest updateTagSuggest(TagSuggest tagSuggest) {
        return tagSuggestRepo.save(tagSuggest);
    }

    @Override
    public void deleteTagSuggest(Long id) {
        tagSuggestRepo.deleteById(id);
    }

    @Override
    public List<TagSuggest> getTagsSuggestions() {
        return tagSuggestRepo.findAll();
    }

    @Override
    public TagSuggest getTagSuggestion(Long id) {
        return tagSuggestRepo.findTagById(id);
    }
}
