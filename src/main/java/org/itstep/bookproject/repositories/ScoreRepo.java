package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Score;
import org.itstep.bookproject.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepo extends JpaRepository<Score, Long> {
    List<Score> findAllByBookId(Long id);
    Score findByBookIdAndUserId(Long book_id, Long user_id);
}
