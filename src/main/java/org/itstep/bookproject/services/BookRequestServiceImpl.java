package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.BookRequest;
import org.itstep.bookproject.entities.RateRequest;
import org.itstep.bookproject.repositories.BookRequestRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class BookRequestServiceImpl implements BookRequestService {

    private final BookRequestRepo bookRequestRepo;

    public BookRequestServiceImpl(BookRequestRepo bookRequestRepo) {
        this.bookRequestRepo = bookRequestRepo;
    }

    @Override
    public BookRequest updateBookRequest(BookRequest bookRequest) {
        return bookRequestRepo.save(bookRequest);
    }

    @Override
    public List<BookRequest> getBooksRequests() {
        return bookRequestRepo.findAll();
    }

    @Override
    public BookRequest getBookRequest(Long id) {
        return bookRequestRepo.getBookRequestById(id);
    }

    @Override
    public List<BookRequest> getBooksRequestsNotApproved(boolean not_approved, Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<BookRequest> bookRequests = bookRequestRepo.findAllByNotApproved(not_approved, paging);

        return bookRequests.getContent();
    }

    @Override
    public List<BookRequest> getAllUserBooksRequestNotApproved(Long id, boolean not_approved, Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<BookRequest> bookRequests = bookRequestRepo.findAllByUserIdAndNotApproved(id, not_approved, paging);

        return bookRequests.getContent();
    }

    @Override
    public List<BookRequest> getAllUserBooksRequestNotApproved(Long id, boolean not_approved) {
        return bookRequestRepo.findAllByUserIdAndNotApproved(id, not_approved);
    }

    @Override
    public Integer getBooksRequestsCountNotApproved(boolean val) {
        return bookRequestRepo.findAllByNotApproved(val).size();
    }

    @Override
    public void removeBooksRequest(Long id) {
        bookRequestRepo.deleteById(id);
    }
}
