package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.repositories.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@EnableWebSecurity
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public DbUser updateUser(DbUser dbUser) {
        return userRepo.save(dbUser);
    }

    @Override
    public DbUser getUser(String email) {
        return userRepo.findDbUserByEmail(email);
    }

    @Override
    public DbUser getUser(Long id) {
        return userRepo.findDbUserById(id);
    }

    @Override
    public DbUser registerUser(DbUser dbUser) {
        DbUser checkDbUser = userRepo.findDbUserByEmail(dbUser.getEmail());
        if(Objects.isNull(checkDbUser)){
            dbUser.setPassword(bCryptPasswordEncoder().encode(dbUser.getPassword()));
            return userRepo.save(dbUser);
        }

        return null;
    }

    @Override
    public boolean comparePasswords(Long id, String newPassword) {
        DbUser user = userRepo.findDbUserById(id);

        if(user != null){
            if(user.getPassword() == bCryptPasswordEncoder().encode(newPassword)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<DbUser> getUsers(Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<DbUser> pagedResult = userRepo.findAll(paging);

        return pagedResult.getContent();
    }

    @Override
    public Integer getUsersCount() {
        return userRepo.findAll().size();
    }


    @Override
    public List<DbUser> findUsers(String keyword) {
        if(keyword != null){
            return userRepo.findAllByUsernameContainingIgnoreCase(keyword);
        }
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DbUser dbUser = userRepo.findDbUserByEmail(email);
        if(Objects.nonNull(dbUser)){
            User securityDbUser = new User(dbUser.getEmail(), dbUser.getPassword(), dbUser.getRoles());
            return securityDbUser;
        }
        return null;
    }
}
