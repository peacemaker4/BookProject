package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Tag;
import org.itstep.bookproject.repositories.TagRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;

    public TagServiceImpl(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    @Override
    public Tag updateTag(Tag tag) {
        return tagRepo.save(tag);
    }

    @Override
    public List<Tag> getTags() {
        return tagRepo.findAll();
    }

    @Override
    public Tag getTag(Long id) {
        return tagRepo.findTagById(id);
    }
}
