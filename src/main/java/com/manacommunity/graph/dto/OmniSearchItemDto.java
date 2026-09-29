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
public class OmniSearchItemDto {
    private String id;
    private GraphEntityType domain;
    private String title;
    private String subtitle;
    private String description;
    private String badge;
    private String avatarUrl;
    private String deepLink;
    private Double relevanceScore;
    private List<String> tags;
}
