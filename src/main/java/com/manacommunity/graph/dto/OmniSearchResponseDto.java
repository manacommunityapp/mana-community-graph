package com.manacommunity.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OmniSearchResponseDto {
    private String query;
    private Long totalResults;
    private Map<String, Long> domainCounts;
    private List<OmniSearchItemDto> people;
    private List<OmniSearchItemDto> events;
    private List<OmniSearchItemDto> sports;
    private List<OmniSearchItemDto> jobs;
    private List<OmniSearchItemDto> businesses;
    private List<OmniSearchItemDto> services;
    private List<OmniSearchItemDto> marketplace;
    private List<OmniSearchItemDto> food;
    private List<OmniSearchItemDto> documents;
    private List<OmniSearchItemDto> vendors;
}
