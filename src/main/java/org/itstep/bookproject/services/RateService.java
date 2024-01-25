package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;
import org.itstep.bookproject.entities.Tag;

import java.util.List;

public interface RateService {
    Rate updateRate(Rate rate);
    List<Rate> getRatings();
    Rate getRate(Long id);
    List<Rate> getAllBookRatings(Long id);
    List<Rate> getAllUserRatings(Long id);
    List<Rate> getTop3UserRatings(Long id);
    List<Rate> getAllUserRatings(Long id, Integer pageNO, Integer pageSize);

}
