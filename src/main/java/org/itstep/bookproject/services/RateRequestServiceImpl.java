package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;
import org.itstep.bookproject.repositories.RateRequestRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class RateRequestServiceImpl implements RateRequestService {

    private final RateRequestRepo rateRequestRepo;

    public RateRequestServiceImpl(RateRequestRepo rateRequestRepo) {
        this.rateRequestRepo = rateRequestRepo;
    }

    @Override
    public RateRequest updateRateRequest(RateRequest rateRequest) {
        return rateRequestRepo.save(rateRequest);
    }

    @Override
    public List<RateRequest> getRatingsRequests(Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<RateRequest> rateRequests = rateRequestRepo.findAll(paging);

        return rateRequests.getContent();
    }

    @Override
    public List<RateRequest> getRatingsRequestsNotApproved(boolean not_approved,Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<RateRequest> rateRequests = rateRequestRepo.findAllByNotApproved(not_approved, paging);

        return rateRequests.getContent();
    }

    @Override
    public RateRequest getRateRequest(Long id) {
        return rateRequestRepo.findRateRequestById(id);
    }

    @Override
    public List<RateRequest> getAllUserRatingsRequest(Long id) {
        return rateRequestRepo.findAllByUserId(id);
    }

    @Override
    public List<RateRequest> getAllUserRatingsRequestNotApproved(Long id, boolean not_approved, Integer pageNO, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNO, pageSize);

        Page<RateRequest> rateRequests = rateRequestRepo.findAllByUserIdAndNotApproved(id, not_approved, paging);

        return rateRequests.getContent();
    }

    @Override
    public List<RateRequest> getAllUserRatingsRequestNotApproved(Long id, boolean not_approved) {
        return rateRequestRepo.findAllByUserIdAndNotApproved(id, not_approved);
    }

    @Override
    public Integer getRateRequestsCountNotApproved(boolean val) {
        return rateRequestRepo.findAllByNotApproved(val).size();
    }

    @Override
    public void removeRateRequest(Long id) {
        rateRequestRepo.deleteById(id);
    }
}
