package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.FavoriteBook;
import org.itstep.bookproject.repositories.FavoriteBookRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class FavoriteBookServiceImpl implements FavoriteBookService {

    private final FavoriteBookRepo favoriteBookRepo;

    public FavoriteBookServiceImpl(FavoriteBookRepo favoriteBookRepo) {
        this.favoriteBookRepo = favoriteBookRepo;
    }

    @Override
    public FavoriteBook updateFavorite(FavoriteBook book) {
        return favoriteBookRepo.save(book);
    }

    @Override
    public List<FavoriteBook> getFavoriteBooksOfUser(Long id) {
        return favoriteBookRepo.findAllByUserId(id);
    }

    @Override
    public FavoriteBook getFavoriteBookById(Long id) {
        return favoriteBookRepo.findFavoriteBookById(id);
    }

    @Override
    public FavoriteBook getFavoriteBookByUserAndBookId(Long user_id, Long book_id) {
        return favoriteBookRepo.findFavoriteBookByUserIdAndBookId(user_id, book_id);
    }

    @Override
    public void removeFavoriteBook(Long id) {
        favoriteBookRepo.deleteById(id);
    }
}
