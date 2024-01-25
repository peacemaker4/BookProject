package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.Rate;

import java.util.List;

public interface BookService {
    Book updateBook(Book book);
    List<Book> getBooks();
    Book getBook(Long id);
    void deleteBook(Long id);
    Integer getBooksCount();
    List<Book> getBooks(Integer pageNO, Integer pageSize);
    List<Book> getBooksDesc(Integer pageNO, Integer pageSize);
    List<Book> getLast3();
    List<Book> findBooks(String keyword);
    List<Book> getAllUserBooks(Long id, Integer pageNO, Integer pageSize);
    List<Book> getAllUserBooks(Long id);
}
