package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.UserProfileVisibilityRequest;
import com.manacommunity.graph.dto.UserProfileVisibilityResponse;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.UserProfileVisibility;
import com.manacommunity.graph.repository.UserProfileVisibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivacyService {

    private final UserProfileVisibilityRepository visibilityRepository;

    public UserProfileVisibility getOrCreateVisibility(Long userId, Long communityId) {
        return visibilityRepository.findByUserIdAndCommunityId(userId, communityId)
                .orElseGet(() -> visibilityRepository.save(
                        UserProfileVisibility.builder()
                                .userId(userId)
                                .communityId(communityId)
                                .build()
                ));
    }

    public UserProfileVisibilityResponse getVisibilityResponse(Long userId, Long communityId) {
        return mapToResponse(getOrCreateVisibility(userId, communityId));
    }

    @Transactional
    public UserProfileVisibilityResponse updateVisibility(Long userId, Long communityId, UserProfileVisibilityRequest req) {
        UserProfileVisibility v = getOrCreateVisibility(userId, communityId);

        if (req.getProfessionVisibility() != null) v.setProfessionVisibility(req.getProfessionVisibility());
        if (req.getSkillsVisibility() != null) v.setSkillsVisibility(req.getSkillsVisibility());
        if (req.getInterestsVisibility() != null) v.setInterestsVisibility(req.getInterestsVisibility());
        if (req.getSportsVisibility() != null) v.setSportsVisibility(req.getSportsVisibility());
        if (req.getServicesVisibility() != null) v.setServicesVisibility(req.getServicesVisibility());
        if (req.getEventsVisibility() != null) v.setEventsVisibility(req.getEventsVisibility());
        if (req.getTripsVisibility() != null) v.setTripsVisibility(req.getTripsVisibility());
        if (req.getPurchasesVisibility() != null) v.setPurchasesVisibility(req.getPurchasesVisibility());
        if (req.getFlatVisibility() != null) v.setFlatVisibility(req.getFlatVisibility());

        return mapToResponse(visibilityRepository.save(v));
    }

    public boolean isRelationshipDiscoverable(Long targetUserId, Long communityId, RelationshipType relType, GraphEntityType targetType) {
        UserProfileVisibility v = getOrCreateVisibility(targetUserId, communityId);

        GraphVisibility setting = GraphVisibility.COMMUNITY;
        if (relType == RelationshipType.WORKS_AS || targetType == GraphEntityType.PROFESSION) {
            setting = v.getProfessionVisibility();
        } else if (relType == RelationshipType.HAS_SKILL || targetType == GraphEntityType.SKILL) {
            setting = v.getSkillsVisibility();
        } else if (relType == RelationshipType.INTERESTED_IN) {
            setting = v.getInterestsVisibility();
        } else if (targetType == GraphEntityType.SPORT) {
            setting = v.getSportsVisibility();
        } else if (relType == RelationshipType.OFFERS_SERVICE || relType == RelationshipType.HOME_CHEF) {
            setting = v.getServicesVisibility();
        } else if (relType == RelationshipType.REGISTERED_FOR || targetType == GraphEntityType.EVENT) {
            setting = v.getEventsVisibility();
        } else if (relType == RelationshipType.BOOKED || targetType == GraphEntityType.TRIP) {
            setting = v.getTripsVisibility();
        } else if (relType == RelationshipType.BOUGHT || targetType == GraphEntityType.PRODUCT) {
            setting = v.getPurchasesVisibility();
        } else if (relType == RelationshipType.LIVES_IN) {
            setting = v.getFlatVisibility();
        }

        return setting == GraphVisibility.COMMUNITY || setting == GraphVisibility.PUBLIC;
    }

    public List<GraphVisibility> getCommunityDiscoverableVisibilities() {
        return List.of(GraphVisibility.COMMUNITY, GraphVisibility.PUBLIC, GraphVisibility.TOWER, GraphVisibility.GROUP);
    }

    private UserProfileVisibilityResponse mapToResponse(UserProfileVisibility v) {
        return UserProfileVisibilityResponse.builder()
                .id(v.getId())
                .communityId(v.getCommunityId())
                .userId(v.getUserId())
                .professionVisibility(v.getProfessionVisibility())
                .skillsVisibility(v.getSkillsVisibility())
                .interestsVisibility(v.getInterestsVisibility())
                .sportsVisibility(v.getSportsVisibility())
                .servicesVisibility(v.getServicesVisibility())
                .eventsVisibility(v.getEventsVisibility())
                .tripsVisibility(v.getTripsVisibility())
                .purchasesVisibility(v.getPurchasesVisibility())
                .flatVisibility(v.getFlatVisibility())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
