package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Score;
import org.itstep.bookproject.repositories.ScoreRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepo scoreRepo;


    public ScoreServiceImpl(ScoreRepo scoreRepo) {
        this.scoreRepo = scoreRepo;
    }


    @Override
    public List<Score> getScoresByBook(Long id) {
        return scoreRepo.findAllByBookId(id);
    }

    @Override
    public Score findScoreByBookIdAndUserId(Long book_id, Long user_id) {
        return scoreRepo.findByBookIdAndUserId(book_id, user_id);
    }

    @Override
    public Score updateScore(Score score) {
        return scoreRepo.save(score);
    }
}
