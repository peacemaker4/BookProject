package org.itstep.bookproject.configs;

import org.itstep.bookproject.entities.Role;

public class StaticConfig {
    public static final Role ROLE_USER = new Role(1L, "ROLE_USER", "User Role");
    public static final Role ROLE_MANAGER = new Role(2L, "ROLE_MOD", "Moderator Role");
    public static final Role ROLE_ADMIN = new Role(3L, "ROLE_ADMIN", "Admin Role");

    private StaticConfig(){

    }
}
