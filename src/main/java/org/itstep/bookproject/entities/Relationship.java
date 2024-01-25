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
public class Relationship extends BaseEntity{

    private Long followerId;

    private Long followingId;

    private LocalDateTime created_at;
}
