package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Book;
import org.itstep.bookproject.entities.Rate;
import org.itstep.bookproject.entities.RateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepo extends JpaRepository<Rate, Long> {
    Rate findRateById(Long id);
    List<Rate> findAllByBookId(Long id);
    List<Rate> findAllByUserId(Long id);
    Page<Rate> findAllByUserId(Long id, Pageable pageable);
    List<Rate> findTop3ByUserIdOrderByIdDesc(Long id);
}
