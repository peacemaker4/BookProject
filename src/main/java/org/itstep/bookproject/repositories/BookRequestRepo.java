package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepo extends JpaRepository<BookRequest, Long> {
    BookRequest getBookRequestById(Long id);
    List<BookRequest> findAllByUserId(Long id);
    Page<BookRequest> findAllByUserIdAndNotApproved(Long id, boolean not_approved, Pageable pageable);
    List<BookRequest> findAllByUserIdAndNotApproved(Long id, boolean not_approved);
    Page<BookRequest> findAllByNotApproved(boolean not_approved, Pageable pageable);
    List<BookRequest> findAllByNotApproved(boolean not_approved);
}
