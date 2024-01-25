package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    Book getBookById(Long id);
    List<Book> findTop3ByOrderByIdDesc();
    List<Book> findAllByNameContainingIgnoreCase(String keyword);
    List<Book> findAllByUserId(Long id);
    Page<Book> findAllByUserId(Long id, Pageable pageable);
}
