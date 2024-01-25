package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.DbUserDetails;
import org.itstep.bookproject.entities.Tag;

import java.util.List;

public interface TagService {
    Tag updateTag(Tag tag);
    List<Tag> getTags();
    Tag getTag(Long id);
}
