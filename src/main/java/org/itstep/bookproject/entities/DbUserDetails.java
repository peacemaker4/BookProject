package org.itstep.bookproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DbUserDetails extends BaseEntity{

    private String about;

    private String profilePicture;

    private String profileWallpaper;
}
