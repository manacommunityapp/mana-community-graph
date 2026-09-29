package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationItemDto {
    private String id;
    private RecommendationType type;
    private String title;
    private String subtitle;
    private String reason; // e.g. "Based on your cricket interest", "Popular in Tower A"
    private Double matchScore;
    private String actionUrl;
    private String imageUrl;
    private Map<String, Object> metadata;
}
