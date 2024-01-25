package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRequestRepo extends JpaRepository<RateRequest, Long> {
    RateRequest findRateRequestById(Long id);
    List<RateRequest> findAllByBookId(Long id);
    List<RateRequest> findAllByUserId(Long id);
    Page<RateRequest> findAllByUserIdAndNotApproved(Long id, boolean not_approved, Pageable pageable);
    List<RateRequest> findAllByUserIdAndNotApproved(Long id, boolean not_approved);
    Page<RateRequest> findAllByNotApproved(boolean not_approved, Pageable pageable);
    List<RateRequest> findAllByNotApproved(boolean not_approved);
}
