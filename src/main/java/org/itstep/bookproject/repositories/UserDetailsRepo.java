package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.DbUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepo extends JpaRepository<DbUserDetails, Long> {
}
