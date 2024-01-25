package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Relationship;
import org.itstep.bookproject.entities.Tag;

import java.util.List;

public interface RelationshipService {
    Relationship updateRelationship(Relationship relationship);
    Relationship findRelationship(Long follower, Long following);
    void deleteRelationship(Long id);
    List<Relationship> findAllByFollower(Long id);
    List<Relationship> findAllByFollowing(Long id);

}
