package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.CommunityProfessionalDto;
import com.manacommunity.graph.dto.CommunityTrendDto;
import com.manacommunity.graph.dto.SmartSearchResultDto;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import com.manacommunity.graph.repository.GraphRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityDiscoveryService {

    private final GraphRelationshipRepository relationshipRepository;
    private final RecommendationService recommendationService;
    private final PrivacyService privacyService;

    public SmartSearchResultDto searchCommunity(Long userId, Long communityId, String query) {
        if (query == null || query.isBlank()) {
            return SmartSearchResultDto.builder()
                    .query("")
                    .totalMatches(0L)
                    .professionals(List.of())
                    .recommendedItems(List.of())
                    .summaryText("Enter a search term to find skills, services, or activities")
                    .build();
        }

        String cleanQuery = query.trim();

        // 1. Search for professionals matching skill or profession
        List<CommunityProfessionalDto> professionals = recommendationService.getCommunityProfessionals(userId, communityId, cleanQuery);

        // 2. Count total matches
        long count = professionals.size();
        String summary = count > 0
                ? String.format("%d community members found matching '%s'", count, cleanQuery)
                : String.format("No direct matches for '%s' in your community", cleanQuery);

        return SmartSearchResultDto.builder()
                .query(cleanQuery)
                .entityType(GraphEntityType.SKILL)
                .totalMatches(count)
                .professionals(professionals)
                .recommendedItems(List.of())
                .summaryText(summary)
                .build();
    }

    public List<CommunityTrendDto> getTopSkills(Long communityId) {
        List<Object[]> list = relationshipRepository.countRelationshipsGroupedByTargetName(
                communityId, RelationshipType.HAS_SKILL);

        List<CommunityTrendDto> results = new ArrayList<>();
        for (Object[] row : list) {
            if (row[0] != null) {
                results.add(CommunityTrendDto.builder()
                        .topic(row[0].toString())
                        .category("Skill")
                        .participantCount((Long) row[1])
                        .description(row[1] + " residents listed " + row[0])
                        .build());
            }
        }
        return results;
    }
}
