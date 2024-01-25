package org.itstep.bookproject.services;

import org.itstep.bookproject.entities.Relationship;
import org.itstep.bookproject.repositories.RelationshipRepo;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableWebSecurity
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipRepo relationshipRepo;

    public RelationshipServiceImpl(RelationshipRepo relationshipRepo) {
        this.relationshipRepo = relationshipRepo;
    }

    @Override
    public Relationship updateRelationship(Relationship relationship) {
        return relationshipRepo.save(relationship);
    }

    @Override
    public Relationship findRelationship(Long follower, Long following) {
        return relationshipRepo.findRelationshipByFollowerIdAndAndFollowingId(follower, following);
    }

    @Override
    public void deleteRelationship(Long id) {
        relationshipRepo.deleteById(id);
    }

    @Override
    public List<Relationship> findAllByFollower(Long id) {
        return relationshipRepo.findAllByFollowerId(id);
    }

    @Override
    public List<Relationship> findAllByFollowing(Long id) {
        return relationshipRepo.findAllByFollowingId(id);
    }
}
