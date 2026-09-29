package com.manacommunity.graph.repository;

import com.manacommunity.graph.model.UserProfileVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileVisibilityRepository extends JpaRepository<UserProfileVisibility, Long> {
    Optional<UserProfileVisibility> findByUserIdAndCommunityId(Long userId, Long communityId);
}
