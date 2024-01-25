package org.itstep.bookproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSuggest extends BaseEntity{
    private String fullName;

    @Lob
    private String description;

    private String authorPicture;

    private Long userId;
}
