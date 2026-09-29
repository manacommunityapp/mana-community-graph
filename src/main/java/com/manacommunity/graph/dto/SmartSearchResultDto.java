package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartSearchResultDto {
    private String query;
    private GraphEntityType entityType;
    private Long totalMatches;
    private List<CommunityProfessionalDto> professionals;
    private List<RecommendationItemDto> recommendedItems;
    private String summaryText; // e.g. "5 Java professionals found in your community"
}
