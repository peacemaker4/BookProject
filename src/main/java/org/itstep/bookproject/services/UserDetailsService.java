package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.DbUserDetails;

public interface UserDetailsService {
    DbUserDetails updateUserDetails(DbUserDetails dbUserDetails);
}
