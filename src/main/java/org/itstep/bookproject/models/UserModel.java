package org.itstep.bookproject.models;

import lombok.Data;

@Data
public class UserModel {
    private String email;

    private String username;

    private String password;

    private String confirmPassword;
}
