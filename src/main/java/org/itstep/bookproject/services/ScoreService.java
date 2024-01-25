package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Score;
import org.itstep.bookproject.entities.Tag;

import java.util.List;

public interface ScoreService {
    List<Score> getScoresByBook(Long id);
    Score findScoreByBookIdAndUserId(Long book_id, Long user_id);
    Score updateScore(Score score);
}
