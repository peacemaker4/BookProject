package org.itstep.bookproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsModel {

    private Long id;

    private String username;

    private String about;

    private String profilePicture;

    private String profileWallpaper;
}
