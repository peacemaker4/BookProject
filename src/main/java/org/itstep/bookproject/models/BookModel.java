package org.itstep.bookproject.models;

import lombok.Data;
import org.itstep.bookproject.entities.Author;
import org.itstep.bookproject.entities.Tag;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
public class BookModel {
    private String name;

    private String description;

    private List<Long> authors_ids;

    private String publisher;

    private Integer pages;

    private Integer year;

    private String cover;

    private Long user_id;

    private List<Long> tags_ids;
}
