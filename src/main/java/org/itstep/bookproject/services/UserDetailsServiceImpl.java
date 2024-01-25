package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.DbUserDetails;
import org.itstep.bookproject.repositories.UserDetailsRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

@Service
@EnableWebSecurity
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;

    public UserDetailsServiceImpl(UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @Override
    public DbUserDetails updateUserDetails(DbUserDetails dbUserDetails) {
        return userDetailsRepo.save(dbUserDetails);
    }
}
