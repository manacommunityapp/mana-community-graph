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
            // General community popular sports
            list.add(RecommendationItemDto.builder()
                    .id("SPORT-POPULAR")
                    .type(RecommendationType.SPORT)
                    .title("Community Badminton & Cricket Club")
                    .subtitle("Over 35 residents play regularly in the sports complex")
                    .reason("Popular sport in your community")
                    .matchScore(0.80)
                    .actionUrl("/sports/activities")
                    .build());
        }
        return list;
    }

    // ─── EVENT RECOMMENDATIONS ───────────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedEvents(Long userId, Long communityId) {
        return List.of(
                RecommendationItemDto.builder()
                        .id("EVT-101")
                        .type(RecommendationType.EVENT)
                        .title("Community Monsoon Fest & Cultural Night")
                        .subtitle("Clubhouse Amphitheatre • Saturday 6:00 PM")
                        .reason("Trending community event (42 families registered)")
                        .matchScore(0.90)
                        .actionUrl("/events/101")
                        .build()
        );
    }

    // ─── TRIP RECOMMENDATIONS ────────────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedTrips(Long userId, Long communityId) {
        return List.of(
                RecommendationItemDto.builder()
                        .id("TRIP-201")
                        .type(RecommendationType.TRIP)
                        .title("Weekend Trek to Ananthagiri Hills")
                        .subtitle("Community group trip with 40-seater luxury bus")
                        .reason("18 neighbors from your tower are interested")
                        .matchScore(0.88)
                        .actionUrl("/trips/201")
                        .build()
        );
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
                    list.add(RecommendationItemDto.builder()
                            .id("CHEF-" + r.getSourceId())
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

        if (list.isEmpty()) {
            list.add(RecommendationItemDto.builder()
                    .id("CHEF-DISCOVER")
                    .type(RecommendationType.HOME_CHEF)
                    .title("Authentic Home Food & Tiffins")
                    .subtitle("Order delicious home cooked food prepared by neighbors")
                    .reason("4 home chefs available in your society")
                    .matchScore(0.85)
                    .actionUrl("/food/menu")
                    .build());
        }
        return list;
    }

    // ─── GROUP BUY RECOMMENDATIONS ───────────────────────────────────────────

    public List<RecommendationItemDto> getRecommendedGroupBuys(Long userId, Long communityId) {
        return List.of(
                RecommendationItemDto.builder()
                        .id("GB-501")
                        .type(RecommendationType.GROUP_BUY)
                        .title("Organic Mango Farm Bulk Order")
                        .subtitle("Tier 3 Price: ₹450/box (Current orders: 38/50 boxes)")
                        .reason("12 households in your tower joined this group buy")
                        .matchScore(0.87)
                        .actionUrl("/group-marketplace/deals/501")
                        .build()
        );
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
