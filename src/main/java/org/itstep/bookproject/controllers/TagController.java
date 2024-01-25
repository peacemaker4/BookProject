package org.itstep.bookproject.controllers;

import org.itstep.bookproject.entities.Tag;
import org.itstep.bookproject.entities.TagSuggest;
import org.itstep.bookproject.models.TagModel;
import org.itstep.bookproject.services.TagService;
import org.itstep.bookproject.services.TagSuggestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TagController {
    private final TagService tagService;
    private final TagSuggestService tagSuggestService;

    public TagController(TagService tagService, TagSuggestService tagSuggestService) {
        this.tagService = tagService;
        this.tagSuggestService = tagSuggestService;
    }

    @GetMapping(value = "/edit/tags/add")
    public String addTag(Model model){
        model.addAttribute("tagModel", new TagModel());
        return "books/add-tag";
    }

    @PostMapping(value = "/edit/tags/add")
    public String addTag(Model model, @ModelAttribute TagModel tagModel){
        if(tagModel != null){
            Tag tag = new Tag(tagModel.getName(), tagModel.getDescription());
            tagService.updateTag(tag);

            return "redirect:/tags/";
        }

        return "books/add-tag";
    }

    @GetMapping(value = "/tags/suggest")
    public String suggestTag(Model model){
        model.addAttribute("tagModel", new TagModel());
        return "books/suggest-tag";
    }

    @PostMapping(value = "/tags/suggest")
    public String suggestTag(Model model, @ModelAttribute TagModel tagModel){
        if(tagModel != null){
            TagSuggest tagSuggest = new TagSuggest(tagModel.getName(), tagModel.getDescription());
            tagSuggestService.updateTagSuggest(tagSuggest);

            return "redirect:/tags/";
        }

        return "books/suggest-tag";
    }

    @GetMapping(value = "/tags")
    public String tagsList(Model model){
        List<Tag> tags = tagService.getTags();
        model.addAttribute("tags", tags);

        return "books/tags-list";
    }
}
