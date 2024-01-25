package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.DbUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<DbUser, Long> {
    DbUser findDbUserByEmail(String email);
    DbUser findDbUserById(Long id);
    List<DbUser> findAllByUsernameContainingIgnoreCase(String keyword);
}
