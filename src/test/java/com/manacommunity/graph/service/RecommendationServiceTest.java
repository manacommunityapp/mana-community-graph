package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.PersonalizedFeedResponse;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.RecommendationType;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import com.manacommunity.graph.repository.GraphRelationshipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceTest {

    @Mock
    private GraphRelationshipRepository relationshipRepository;

    @Mock
    private PrivacyService privacyService;

    @InjectMocks
    private RecommendationService recommendationService;

    private final Long userId = 42L;
    private final Long communityId = 1L;

    @Test
    @DisplayName("Generate personalized feed combining sports, events, trips, chefs, and professionals from graph relationships")
    void testGetPersonalizedFeed_Success() {
        GraphRelationship sportRel = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("42")
                .sourceName("Sandeep")
                .targetType(GraphEntityType.SPORT)
                .targetId("BADMINTON")
                .targetName("Badminton")
                .relationshipType(RelationshipType.MEMBER_OF)
                .build();

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.MEMBER_OF))
                .thenReturn(List.of(sportRel));

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.REGISTERED_FOR))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.INTERESTED_IN))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.BOOKED))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.BOUGHT))
                .thenReturn(List.of());

        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.REGISTERED_FOR, GraphEntityType.EVENT))
                .thenReturn(List.<Object[]>of(new Object[]{"EVT-900", "Annual Sports Meet", 25L}));

        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.BOOKED, GraphEntityType.TRIP))
                .thenReturn(List.<Object[]>of(new Object[]{"TRIP-800", "Araku Valley Weekend", 15L}));

        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.BOUGHT, GraphEntityType.GROUP_BUY))
                .thenReturn(List.<Object[]>of(new Object[]{"GB-700", "Organic Vegetables Basket", 30L}));

        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.HOME_CHEF))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.HAS_SKILL))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.WORKS_AS))
                .thenReturn(List.of());

        when(relationshipRepository.countRelationshipsGroupedByTargetName(communityId, RelationshipType.HAS_SKILL))
                .thenReturn(List.of());

        PersonalizedFeedResponse feed = recommendationService.getPersonalizedFeed(userId, communityId);

        assertNotNull(feed);
        assertEquals(userId, feed.getUserId());
        assertEquals(communityId, feed.getCommunityId());
        assertFalse(feed.getRecommendations().isEmpty());

        // Verify sport recommendation exists from user's joined sports
        boolean hasSport = feed.getRecommendations().stream()
                .anyMatch(r -> r.getType() == RecommendationType.SPORT && r.getTitle().contains("Badminton"));
        assertTrue(hasSport, "Personalized feed should contain recommended badminton sport club");

        // Verify event recommendation derived from real popular events in graph
        boolean hasEvent = feed.getRecommendations().stream()
                .anyMatch(r -> r.getType() == RecommendationType.EVENT && r.getTitle().equals("Annual Sports Meet"));
        assertTrue(hasEvent, "Personalized feed should contain recommended community event from graph");

        // Verify trip recommendation derived from real booked trips in graph
        boolean hasTrip = feed.getRecommendations().stream()
                .anyMatch(r -> r.getType() == RecommendationType.TRIP && r.getTitle().equals("Araku Valley Weekend"));
        assertTrue(hasTrip, "Personalized feed should contain recommended trip from graph");

        // Verify group buy recommendation derived from real group buys in graph
        boolean hasGroupBuy = feed.getRecommendations().stream()
                .anyMatch(r -> r.getType() == RecommendationType.GROUP_BUY && r.getTitle().equals("Organic Vegetables Basket"));
        assertTrue(hasGroupBuy, "Personalized feed should contain recommended group buy from graph");
    }

    @Test
    @DisplayName("Empty graph state produces no fabricated sample recommendations")
    void testEmptyGraph_ProducesNoSampleFallback() {
        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.MEMBER_OF))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.REGISTERED_FOR))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.INTERESTED_IN))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.BOOKED))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.BOUGHT))
                .thenReturn(List.of());

        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.MEMBER_OF, GraphEntityType.SPORT))
                .thenReturn(List.of());
        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.REGISTERED_FOR, GraphEntityType.EVENT))
                .thenReturn(List.of());
        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.BOOKED, GraphEntityType.TRIP))
                .thenReturn(List.of());
        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.BOUGHT, GraphEntityType.GROUP_BUY))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.HOME_CHEF))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.HAS_SKILL))
                .thenReturn(List.of());
        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.WORKS_AS))
                .thenReturn(List.of());
        when(relationshipRepository.countRelationshipsGroupedByTargetName(communityId, RelationshipType.HAS_SKILL))
                .thenReturn(List.of());

        PersonalizedFeedResponse feed = recommendationService.getPersonalizedFeed(userId, communityId);

        assertNotNull(feed);
        assertTrue(feed.getRecommendations().isEmpty(), "When graph has no relationships, recommendations should be empty and not contain hardcoded dummy cards");
        assertTrue(feed.getTrendingInterests().isEmpty());
    }

    @Test
    @DisplayName("Personalized event scoring boosts match score when matching user interest")
    void testEventScoring_WithInterestAffinity() {
        GraphRelationship interestRel = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("42")
                .targetType(GraphEntityType.CLUB)
                .targetName("Cricket")
                .relationshipType(RelationshipType.INTERESTED_IN)
                .build();

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.REGISTERED_FOR))
                .thenReturn(List.of());

        when(relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, "42", RelationshipType.INTERESTED_IN))
                .thenReturn(List.of(interestRel));

        when(relationshipRepository.findPopularTargets(communityId, RelationshipType.REGISTERED_FOR, GraphEntityType.EVENT))
                .thenReturn(List.<Object[]>of(
                        new Object[]{"EVT-1", "Society Cricket Premier League", 50L},
                        new Object[]{"EVT-2", "Baking Workshop", 10L}
                ));

        var events = recommendationService.getRecommendedEvents(userId, communityId);

        assertEquals(2, events.size());
        assertEquals("EVT-1", events.get(0).getId());
        assertEquals(0.95, events.get(0).getMatchScore(), 0.001, "Cricket event should receive affinity boost score of 0.95");
        assertEquals(0.85, events.get(1).getMatchScore(), 0.001, "Baking event should have standard score of 0.85");
    }

    @Test
    @DisplayName("Verified Home Chefs are recommended respecting privacy setting")
    void testHomeChefs_PrivacyRespected() {
        GraphRelationship chefPublic = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("101")
                .sourceName("Chef Priya")
                .targetType(GraphEntityType.HOME_CHEF)
                .targetName("South Indian Delights")
                .relationshipType(RelationshipType.HOME_CHEF)
                .build();

        GraphRelationship chefPrivate = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("102")
                .sourceName("Chef Rahul")
                .targetType(GraphEntityType.HOME_CHEF)
                .targetName("North Indian Thali")
                .relationshipType(RelationshipType.HOME_CHEF)
                .build();

        when(relationshipRepository.findByCommunityIdAndRelationshipType(communityId, RelationshipType.HOME_CHEF))
                .thenReturn(List.of(chefPublic, chefPrivate));

        when(privacyService.isRelationshipDiscoverable(101L, communityId, RelationshipType.HOME_CHEF, GraphEntityType.HOME_CHEF))
                .thenReturn(true);
        when(privacyService.isRelationshipDiscoverable(102L, communityId, RelationshipType.HOME_CHEF, GraphEntityType.HOME_CHEF))
                .thenReturn(false);

        var chefs = recommendationService.getRecommendedHomeChefs(userId, communityId);

        assertEquals(1, chefs.size());
        assertEquals("CHEF-101", chefs.get(0).getId());
        assertEquals("Chef Priya (Home Kitchen)", chefs.get(0).getTitle());
    }
}