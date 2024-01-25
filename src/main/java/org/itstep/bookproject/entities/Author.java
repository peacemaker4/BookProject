package org.itstep.bookproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Author extends BaseEntity{
    private String fullName;

    @Lob
    private String description;

    private String authorPicture;

    private Long userId;
}
