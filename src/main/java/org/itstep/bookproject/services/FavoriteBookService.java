package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.FavoriteBook;
import org.itstep.bookproject.entities.Tag;

import java.util.List;

public interface FavoriteBookService {
    FavoriteBook updateFavorite(FavoriteBook book);
    List<FavoriteBook> getFavoriteBooksOfUser(Long id);
    FavoriteBook getFavoriteBookById(Long id);
    FavoriteBook getFavoriteBookByUserAndBookId(Long user_id, Long book_id);
    void removeFavoriteBook(Long id);
}
