package com.manacommunity.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityTrendDto {
    private String topic; // e.g. "Cricket", "Java", "Trekking"
    private String category; // e.g. "Sports", "Technology", "Travel"
    private Long participantCount;
    private String description;
}
