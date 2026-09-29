package com.manacommunity.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizedFeedResponse {
    private Long userId;
    private Long communityId;
    private List<RecommendationItemDto> recommendations;
    private List<CommunityTrendDto> trendingInterests;
}
