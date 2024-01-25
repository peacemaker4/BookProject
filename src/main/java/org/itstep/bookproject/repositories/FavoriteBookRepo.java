package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.FavoriteBook;
import org.itstep.bookproject.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteBookRepo extends JpaRepository<FavoriteBook, Long> {
    FavoriteBook findFavoriteBookById(Long id);
    FavoriteBook findFavoriteBookByUserIdAndBookId(Long user_id, Long book_id);
    List<FavoriteBook> findAllByUserId(Long id);
}
