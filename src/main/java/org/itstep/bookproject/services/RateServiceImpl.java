package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;
import org.itstep.bookproject.repositories.RateRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class RateServiceImpl implements RateService {
    private final RateRepo rateRepo;

    public RateServiceImpl(RateRepo rateRepo) {
        this.rateRepo = rateRepo;
    }

    @Override
    public Rate updateRate(Rate rate) {
        return rateRepo.save(rate);
    }

    @Override
    public List<Rate> getRatings() {
        return rateRepo.findAll();
    }

    @Override
    public Rate getRate(Long id) {

        return rateRepo.findRateById(id);
    }

    @Override
    public List<Rate> getAllBookRatings(Long id) {
        return rateRepo.findAllByBookId(id);
    }

    @Override
    public List<Rate> getAllUserRatings(Long id) {
        return rateRepo.findAllByUserId(id);
    }

    @Override
    public List<Rate> getTop3UserRatings(Long id) {
        return rateRepo.findTop3ByUserIdOrderByIdDesc(id);
    }

    @Override
    public List<Rate> getAllUserRatings(Long id, Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<Rate> ratings = rateRepo.findAllByUserId(id, paging);

        return ratings.getContent();
    }
}
