package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.BookRequest;
import org.itstep.bookproject.entities.RateRequest;

import java.util.List;

public interface BookRequestService {
    BookRequest updateBookRequest(BookRequest bookRequest);
    List<BookRequest> getBooksRequests();
    BookRequest getBookRequest(Long id);
    List<BookRequest> getBooksRequestsNotApproved(boolean not_approved, Integer pageNO, Integer pageSize);
    List<BookRequest> getAllUserBooksRequestNotApproved(Long id, boolean not_approved, Integer pageNO, Integer pageSize);
    List<BookRequest> getAllUserBooksRequestNotApproved(Long id, boolean not_approved);
    Integer getBooksRequestsCountNotApproved(boolean val);
    void removeBooksRequest(Long id);
}
