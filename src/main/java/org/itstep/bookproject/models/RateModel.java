package org.itstep.bookproject.models;

import lombok.Data;

import java.util.Date;

@Data
public class RateModel {

    private String title;

    private String description;

    private Long book_id;

    private Long user_id;

    private Date created_at;
}
