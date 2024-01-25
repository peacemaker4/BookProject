package org.itstep.bookproject.repositories;

import org.itstep.bookproject.entities.Relationship;
import org.itstep.bookproject.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepo extends JpaRepository<Relationship, Long> {
    Relationship findRelationshipByFollowerIdAndAndFollowingId(Long follower, Long following);
    List<Relationship> findAllByFollowerId(Long id);
    List<Relationship> findAllByFollowingId(Long id);

}
