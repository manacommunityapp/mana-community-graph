package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.*;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RecommendationType;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import com.manacommunity.graph.repository.GraphRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final GraphRelationshipRepository relationshipRepository;
    private final PrivacyService privacyService;

    // ─── PERSONALIZED HOME FEED ──────────────────────────────────────────────

    public PersonalizedFeedResponse getPersonalizedFeed(Long userId, Long communityId) {
        List<RecommendationItemDto> items = new ArrayList<>();

        // 1. Recommended Sports
        items.addAll(getRecommendedSports(userId, communityId));

        // 2. Recommended Events
        items.addAll(getRecommendedEvents(userId, communityId));

        // 3. Recommended Trips
        items.addAll(getRecommendedTrips(userId, communityId));

        // 4. Recommended Home Chefs
        items.addAll(getRecommendedHomeChefs(userId, communityId));

        // 5. Recommended Group Buys
        items.addAll(getRecommendedGroupBuys(userId, communityId));

        // 6. Community Professionals
        items.addAll(getTopCommunityProfessionalsCards(userId, communityId));

        // 7. Trending Interests
        List<CommunityTrendDto> trends = getCommunityTrends(communityId);

        return PersonalizedFeedResponse.builder()
                .userId(userId)
                .communityId(communityId)
                .recommendations(items)
                .trendingInterests(trends)
                .build();
    }

    // ─── SPORTS RECOMMENDATIONS ──────────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedSports(Long userId, Long communityId) {
        String uid = userId.toString();
        List<GraphRelationship> userSports = relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, uid, RelationshipType.MEMBER_OF);

        List<RecommendationItemDto> list = new ArrayList<>();
        if (!userSports.isEmpty()) {
            for (GraphRelationship rel : userSports) {
                list.add(RecommendationItemDto.builder()
                        .id("SPORT-" + rel.getTargetId())
                        .type(RecommendationType.SPORT)
                        .title(rel.getTargetName() + " Tournament & Matchups")
                        .subtitle("Connect with other " + rel.getTargetName() + " players in your community")
                        .reason("Based on your interest in " + rel.getTargetName())
                        .matchScore(0.95)
                        .actionUrl("/sports/tournaments?sport=" + rel.getTargetName())
                        .build());
            }
        } else {
            // General community popular sports from real graph relationships
            List<Object[]> popularSports = relationshipRepository.findPopularTargets(
                    communityId, RelationshipType.MEMBER_OF, GraphEntityType.SPORT);

            for (Object[] row : popularSports) {
                String sportId = row[0] != null ? row[0].toString() : "UNKNOWN";
                String sportName = row[1] != null ? row[1].toString() : sportId;
                long count = row[2] instanceof Number ? ((Number) row[2]).longValue() : 0L;

                list.add(RecommendationItemDto.builder()
                        .id("SPORT-" + sportId)
                        .type(RecommendationType.SPORT)
                        .title("Community " + sportName + " Club")
                        .subtitle(count + " residents actively participate in your community")
                        .reason("Popular sport in your community")
                        .matchScore(0.80)
                        .actionUrl("/sports/activities?sport=" + sportName)
                        .build());
            }
        }
        return list;
    }

    // ─── EVENT RECOMMENDATIONS ───────────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedEvents(Long userId, Long communityId) {
        String uid = userId.toString();
        // Fetch events the user already registered for so we don't redundantly recommend them
        Set<String> alreadyRegisteredEventIds = relationshipRepository
                .findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                        communityId, GraphEntityType.USER, uid, RelationshipType.REGISTERED_FOR)
                .stream()
                .map(GraphRelationship::getTargetId)
                .collect(Collectors.toSet());

        // Check user interests / clubs to personalize scoring
        Set<String> userInterests = relationshipRepository
                .findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                        communityId, GraphEntityType.USER, uid, RelationshipType.INTERESTED_IN)
                .stream()
                .map(r -> r.getTargetName().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        List<Object[]> popularEvents = relationshipRepository.findPopularTargets(
                communityId, RelationshipType.REGISTERED_FOR, GraphEntityType.EVENT);

        List<RecommendationItemDto> list = new ArrayList<>();
        for (Object[] row : popularEvents) {
            String eventId = row[0] != null ? row[0].toString() : "";
            if (alreadyRegisteredEventIds.contains(eventId)) {
                continue;
            }
            String eventName = row[1] != null ? row[1].toString() : "Community Event";
            long attendeeCount = row[2] instanceof Number ? ((Number) row[2]).longValue() : 0L;

            boolean hasInterestMatch = userInterests.stream()
                    .anyMatch(interest -> eventName.toLowerCase(Locale.ROOT).contains(interest));
            double matchScore = hasInterestMatch ? 0.95 : 0.85;

            String recId = eventId.startsWith("EVT-") ? eventId : "EVT-" + eventId;

            list.add(RecommendationItemDto.builder()
                    .id(recId)
                    .type(RecommendationType.EVENT)
                    .title(eventName)
                    .subtitle("Community Event • " + attendeeCount + " neighbors registered")
                    .reason(hasInterestMatch ? "Matches your interests (" + attendeeCount + " neighbors registered)"
                            : "Trending community event (" + attendeeCount + " neighbors registered)")
                    .matchScore(matchScore)
                    .actionUrl("/events/" + eventId)
                    .build());
        }
        return list;
    }

    // ─── TRIP RECOMMENDATIONS ────────────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedTrips(Long userId, Long communityId) {
        String uid = userId.toString();
        Set<String> bookedTripIds = relationshipRepository
                .findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                        communityId, GraphEntityType.USER, uid, RelationshipType.BOOKED)
                .stream()
                .map(GraphRelationship::getTargetId)
                .collect(Collectors.toSet());

        List<Object[]> popularTrips = relationshipRepository.findPopularTargets(
                communityId, RelationshipType.BOOKED, GraphEntityType.TRIP);

        List<RecommendationItemDto> list = new ArrayList<>();
        for (Object[] row : popularTrips) {
            String tripId = row[0] != null ? row[0].toString() : "";
            if (bookedTripIds.contains(tripId)) {
                continue;
            }
            String tripName = row[1] != null ? row[1].toString() : "Community Trip";
            long participantCount = row[2] instanceof Number ? ((Number) row[2]).longValue() : 0L;

            String recId = tripId.startsWith("TRIP-") ? tripId : "TRIP-" + tripId;

            list.add(RecommendationItemDto.builder()
                    .id(recId)
                    .type(RecommendationType.TRIP)
                    .title(tripName)
                    .subtitle("Community group trip (" + participantCount + " participants joined)")
                    .reason(participantCount + " neighbors from your community have booked this trip")
                    .matchScore(0.88)
                    .actionUrl("/trips/" + tripId)
                    .build());
        }
        return list;
    }

    // ─── HOME CHEF RECOMMENDATIONS ───────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedHomeChefs(Long userId, Long communityId) {
        List<GraphRelationship> chefRel = relationshipRepository.findByCommunityIdAndRelationshipType(
                communityId, RelationshipType.HOME_CHEF);

        List<RecommendationItemDto> list = new ArrayList<>();
        for (GraphRelationship r : chefRel) {
            try {
                Long chefUserId = Long.parseLong(r.getSourceId());
                if (privacyService.isRelationshipDiscoverable(chefUserId, communityId, RelationshipType.HOME_CHEF, GraphEntityType.HOME_CHEF)) {
                    String chefId = r.getSourceId().startsWith("CHEF-") ? r.getSourceId() : "CHEF-" + r.getSourceId();
                    list.add(RecommendationItemDto.builder()
                            .id(chefId)
                            .type(RecommendationType.HOME_CHEF)
                            .title(r.getSourceName() + " (Home Kitchen)")
                            .subtitle(r.getTargetName() + " • Fresh homemade meals & treats")
                            .reason("Verified home chef in your community")
                            .matchScore(0.92)
                            .actionUrl("/food/chefs/" + r.getSourceId())
                            .build());
                }
            } catch (Exception ignored) {}
        }
        return list;
    }

    // ─── GROUP BUY RECOMMENDATIONS ───────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedGroupBuys(Long userId, Long communityId) {
        String uid = userId.toString();
        Set<String> joinedGroupBuyIds = relationshipRepository
                .findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                        communityId, GraphEntityType.USER, uid, RelationshipType.BOUGHT)
                .stream()
                .map(GraphRelationship::getTargetId)
                .collect(Collectors.toSet());

        List<Object[]> popularGroupBuys = relationshipRepository.findPopularTargets(
                communityId, RelationshipType.BOUGHT, GraphEntityType.GROUP_BUY);

        List<RecommendationItemDto> list = new ArrayList<>();
        for (Object[] row : popularGroupBuys) {
            String gbId = row[0] != null ? row[0].toString() : "";
            if (joinedGroupBuyIds.contains(gbId)) {
                continue;
            }
            String gbName = row[1] != null ? row[1].toString() : "Community Bulk Order";
            long buyersCount = row[2] instanceof Number ? ((Number) row[2]).longValue() : 0L;

            String recId = gbId.startsWith("GB-") ? gbId : "GB-" + gbId;

            list.add(RecommendationItemDto.builder()
                    .id(recId)
                    .type(RecommendationType.GROUP_BUY)
                    .title(gbName)
                    .subtitle("Bulk Order Deal • " + buyersCount + " orders joined")
                    .reason(buyersCount + " households in your community joined this group buy")
                    .matchScore(0.87)
                    .actionUrl("/group-marketplace/deals/" + gbId)
                    .build());
        }
        return list;
    }

    // ─── COMMUNITY PROFESSIONALS CARDS ───────────────────────────────────────

    public List<RecommendationItemDto> getTopCommunityProfessionalsCards(Long userId, Long communityId) {
        List<CommunityProfessionalDto> pros = getCommunityProfessionals(userId, communityId, null);
        List<RecommendationItemDto> list = new ArrayList<>();

        if (!pros.isEmpty()) {
            CommunityProfessionalDto top = pros.get(0);
            list.add(RecommendationItemDto.builder()
                    .id("PRO-" + top.getUserId())
                    .type(RecommendationType.COMMUNITY_PROFESSIONAL)
                    .title(top.getName() + " — " + top.getProfession())
                    .subtitle("Skills: " + String.join(", ", top.getSkills()))
                    .reason("Community verified professional in " + top.getTower())
                    .matchScore(0.85)
                    .actionUrl("/network/profile/" + top.getUserId())
                    .build());
        }
        return list;
    }

    // ─── COMMUNITY PROFESSIONALS DIRECTORY ───────────────────────────────────

    public List<CommunityProfessionalDto> getCommunityProfessionals(Long requesterUserId, Long communityId, String skillOrProfession) {
        List<GraphRelationship> skillRels = relationshipRepository.findByCommunityIdAndRelationshipType(
                communityId, RelationshipType.HAS_SKILL);

        Map<String, List<String>> userSkillsMap = new HashMap<>();
        Map<String, String> userNameMap = new HashMap<>();

        for (GraphRelationship r : skillRels) {
            try {
                Long targetUid = Long.parseLong(r.getSourceId());
                if (privacyService.isRelationshipDiscoverable(targetUid, communityId, RelationshipType.HAS_SKILL, GraphEntityType.SKILL)) {
                    userSkillsMap.computeIfAbsent(r.getSourceId(), k -> new ArrayList<>()).add(r.getTargetName());
                    userNameMap.put(r.getSourceId(), r.getSourceName());
                }
            } catch (Exception ignored) {}
        }

        // Get professions
        List<GraphRelationship> profRels = relationshipRepository.findByCommunityIdAndRelationshipType(
                communityId, RelationshipType.WORKS_AS);
        Map<String, String> userProfMap = new HashMap<>();
        for (GraphRelationship r : profRels) {
            try {
                Long targetUid = Long.parseLong(r.getSourceId());
                if (privacyService.isRelationshipDiscoverable(targetUid, communityId, RelationshipType.WORKS_AS, GraphEntityType.PROFESSION)) {
                    userProfMap.put(r.getSourceId(), r.getTargetName());
                    userNameMap.put(r.getSourceId(), r.getSourceName());
                }
            } catch (Exception ignored) {}
        }

        List<CommunityProfessionalDto> result = new ArrayList<>();
        for (String uid : userNameMap.keySet()) {
            String name = userNameMap.get(uid);
            String prof = userProfMap.getOrDefault(uid, "Professional");
            List<String> skills = userSkillsMap.getOrDefault(uid, List.of());

            if (skillOrProfession != null && !skillOrProfession.isBlank()) {
                String query = skillOrProfession.toLowerCase(Locale.ROOT);
                boolean matchesSkill = skills.stream().anyMatch(s -> s.toLowerCase(Locale.ROOT).contains(query));
                boolean matchesProf = prof.toLowerCase(Locale.ROOT).contains(query);
                if (!matchesSkill && !matchesProf) {
                    continue;
                }
            }

            result.add(CommunityProfessionalDto.builder()
                    .userId(uid)
                    .name(name)
                    .tower("Community Resident")
                    .flatNumber("")
                    .profession(prof)
                    .skills(skills)
                    .servicesOffered(List.of())
                    .verifiedCommunityMember(true)
                    .build());
        }
        return result;
    }

    // ─── PEOPLE YOU MAY KNOW ─────────────────────────────────────────────────

    public List<RecommendationItemDto> getPeopleYouMayKnow(Long userId, Long communityId) {
        String uid = userId.toString();
        List<GraphRelationship> mySports = relationshipRepository.findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
                communityId, GraphEntityType.USER, uid, RelationshipType.MEMBER_OF);

        List<RecommendationItemDto> connections = new ArrayList<>();
        Set<String> suggestedUserIds = new HashSet<>();

        for (GraphRelationship myRel : mySports) {
            // Find other users in the same sport/club
            List<GraphRelationship> peers = relationshipRepository.findByCommunityIdAndRelationshipTypeAndTargetTypeAndTargetId(
                    communityId, RelationshipType.MEMBER_OF, myRel.getTargetType(), myRel.getTargetId());

            for (GraphRelationship peer : peers) {
                if (!peer.getSourceId().equals(uid) && !suggestedUserIds.contains(peer.getSourceId())) {
                    suggestedUserIds.add(peer.getSourceId());
                    connections.add(RecommendationItemDto.builder()
                            .id("PYMK-" + peer.getSourceId())
                            .type(RecommendationType.PEOPLE_YOU_MAY_KNOW)
                            .title(peer.getSourceName())
                            .subtitle("Plays " + myRel.getTargetName() + " in your community")
                            .reason("You both play " + myRel.getTargetName())
                            .matchScore(0.85)
                            .actionUrl("/network/profile/" + peer.getSourceId())
                            .build());
                }
            }
        }
        return connections;
    }

    // ─── TRENDS ──────────────────────────────────────────────────────────────

    public List<CommunityTrendDto> getCommunityTrends(Long communityId) {
        List<Object[]> popularSkills = relationshipRepository.countRelationshipsGroupedByTargetName(
                communityId, RelationshipType.HAS_SKILL);

        List<CommunityTrendDto> trends = new ArrayList<>();
        for (Object[] row : popularSkills) {
            if (row[0] != null) {
                trends.add(CommunityTrendDto.builder()
                        .topic(row[0].toString())
                        .category("Skills & Profession")
                        .participantCount((Long) row[1])
                        .description(row[1] + " residents have listed " + row[0])
                        .build());
            }
        }
        return trends;
    }
}
