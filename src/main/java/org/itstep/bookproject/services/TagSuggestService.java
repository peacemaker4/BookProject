package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Tag;
import org.itstep.bookproject.entities.TagSuggest;

import java.util.List;

public interface TagSuggestService {
    TagSuggest updateTagSuggest(TagSuggest tagSuggest);
    void deleteTagSuggest(Long id);
    List<TagSuggest> getTagsSuggestions();
    TagSuggest getTagSuggestion(Long id);
}
