package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.DbUserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    DbUser updateUser(DbUser dbUser);

    DbUser getUser(String email);

    DbUser getUser(Long id);

    DbUser registerUser(DbUser dbUser);

    List<DbUser> getUsers(Integer pageNO, Integer pageSize);

    Integer getUsersCount();

    boolean comparePasswords(Long id, String newPassword);

    List<DbUser> findUsers(String keyword);
}
