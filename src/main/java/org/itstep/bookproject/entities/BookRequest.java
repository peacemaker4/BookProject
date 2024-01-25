package org.itstep.bookproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest extends BaseEntity{
    private String name;

    @Lob
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Author> authors;

    private String publisher;

    private Integer pages;

    private Integer year;

    private String cover;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private DbUser user;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Tag> tags;

    private String file;

    private String fileType;

    private LocalDateTime created_at;

    private boolean notApproved;

    private String commentary;
}
