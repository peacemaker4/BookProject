package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.repositories.BookRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;

    public BookServiceImpl(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepo.save(book);
    }

    @Override
    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    @Override
    public Book getBook(Long id) {
        return bookRepo.getBookById(id);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepo.deleteById(id);
    }

    @Override
    public Integer getBooksCount() {
        return bookRepo.findAll().size();
    }

    @Override
    public List<Book> getBooks(Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<Book> pagedResult = bookRepo.findAll(paging);

        return pagedResult.getContent();
    }

    @Override
    public List<Book> getBooksDesc(Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize, Sort.by("id").descending());

        Page<Book> pagedResult = bookRepo.findAll(paging);

        return pagedResult.getContent();
    }

    @Override
    public List<Book> getLast3() {
        return bookRepo.findTop3ByOrderByIdDesc();
    }

    @Override
    public List<Book> findBooks(String keyword) {
        if(keyword != null){
            return bookRepo.findAllByNameContainingIgnoreCase(keyword);
        }
        return bookRepo.findAll();
    }

    @Override
    public List<Book> getAllUserBooks(Long id, Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<Book> books = bookRepo.findAllByUserId(id, paging);

        return books.getContent();
    }

    @Override
    public List<Book> getAllUserBooks(Long id) {
        return bookRepo.findAllByUserId(id);
    }
}
