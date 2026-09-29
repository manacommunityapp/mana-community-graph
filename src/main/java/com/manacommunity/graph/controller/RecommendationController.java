package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.CommunityProfessionalDto;
import com.manacommunity.graph.dto.PersonalizedFeedResponse;
import com.manacommunity.graph.dto.RecommendationItemDto;
import com.manacommunity.graph.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/graph/recommendations", "/api/graph/discover", "/graph/discover", "/community-graph/discover", "/discover/recommendations"})
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping({"", "/", "/feed", "/discover", "/all"})
    public ResponseEntity<PersonalizedFeedResponse> getPersonalizedFeed(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal != null && principal.getAppUser() != null && principal.getAppUser().getCommunity() != null
                ? principal.getAppUser().getCommunity().getId()
                : 1L;
        Long userId = principal != null ? principal.getId() : 1L;
        return ResponseEntity.ok(recommendationService.getPersonalizedFeed(userId, communityId));
    }

    @GetMapping("/sports")
    public ResponseEntity<List<RecommendationItemDto>> getSportsRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedSports(getUserId(principal), getCommunityId(principal)));
    }

    @GetMapping("/events")
    public ResponseEntity<List<RecommendationItemDto>> getEventRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedEvents(getUserId(principal), getCommunityId(principal)));
    }

    @GetMapping("/trips")
    public ResponseEntity<List<RecommendationItemDto>> getTripRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedTrips(getUserId(principal), getCommunityId(principal)));
    }

    @GetMapping("/home-chefs")
    public ResponseEntity<List<RecommendationItemDto>> getHomeChefRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedHomeChefs(getUserId(principal), getCommunityId(principal)));
    }

    @GetMapping("/group-buys")
    public ResponseEntity<List<RecommendationItemDto>> getGroupBuyRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedGroupBuys(getUserId(principal), getCommunityId(principal)));
    }

    @GetMapping("/professionals")
    public ResponseEntity<List<CommunityProfessionalDto>> getProfessionals(
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getCommunityProfessionals(getUserId(principal), getCommunityId(principal), query));
    }

    @GetMapping("/people-you-may-know")
    public ResponseEntity<List<RecommendationItemDto>> getPeopleYouMayKnow(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(recommendationService.getPeopleYouMayKnow(getUserId(principal), getCommunityId(principal)));
    }

    private Long getUserId(UserPrincipal principal) {
        return principal != null ? principal.getId() : 1L;
    }

    private Long getCommunityId(UserPrincipal principal) {
        return principal != null && principal.getAppUser() != null && principal.getAppUser().getCommunity() != null
                ? principal.getAppUser().getCommunity().getId()
                : 1L;
    }
}
