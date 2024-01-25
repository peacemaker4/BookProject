package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;

import java.util.List;

public interface RateRequestService {
    RateRequest updateRateRequest(RateRequest rate);
    List<RateRequest> getRatingsRequests(Integer pageNO, Integer pageSize);
    List<RateRequest> getRatingsRequestsNotApproved(boolean not_approved, Integer pageNO, Integer pageSize);
    RateRequest getRateRequest(Long id);
    List<RateRequest> getAllUserRatingsRequest(Long id);
    List<RateRequest> getAllUserRatingsRequestNotApproved(Long id, boolean not_approved, Integer pageNO, Integer pageSize);
    List<RateRequest> getAllUserRatingsRequestNotApproved(Long id, boolean not_approved);
    Integer getRateRequestsCountNotApproved(boolean val);
    void removeRateRequest(Long id);
}
