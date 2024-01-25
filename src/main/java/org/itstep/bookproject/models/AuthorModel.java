package org.itstep.bookproject.models;

import lombok.Data;

import java.util.List;

@Data
public class AuthorModel {
    private String fullName;

    private String description;

    private String authorPicture;

    private Long userId;
}
