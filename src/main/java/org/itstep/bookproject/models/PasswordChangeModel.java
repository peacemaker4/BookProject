package org.itstep.bookproject.models;

import lombok.Data;

@Data
public class PasswordChangeModel {
    private String oldPassword;

    private String newPassword;
}
