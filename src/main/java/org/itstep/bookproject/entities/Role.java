package org.itstep.bookproject.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
public class Role extends BaseEntity implements GrantedAuthority {

    private String role;

    private String description;

    public Role(Long id, String role_user, String description){
        this.setId(id);
        this.role = role_user;
        this.description = description;
    }


    @Override
    public String getAuthority() {
        return getRole();
    }
}
