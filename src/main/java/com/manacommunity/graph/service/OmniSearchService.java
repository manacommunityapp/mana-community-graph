package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.OmniSearchItemDto;
import com.manacommunity.graph.dto.OmniSearchResponseDto;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphNodeTaxonomy;
import com.manacommunity.graph.model.GraphRelationship;
import com.manacommunity.graph.repository.GraphNodeTaxonomyRepository;
import com.manacommunity.graph.repository.GraphRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OmniSearchService {

    private final GraphRelationshipRepository relationshipRepository;
    private final GraphNodeTaxonomyRepository taxonomyRepository;

    public OmniSearchResponseDto performOmniSearch(Long userId, Long communityId, String query, Integer limitPerDomain) {
        if (query == null || query.trim().isEmpty()) {
            return OmniSearchResponseDto.builder()
                    .query("")
                    .totalResults(0L)
                    .domainCounts(Collections.emptyMap())
                    .people(Collections.emptyList())
                    .events(Collections.emptyList())
                    .sports(Collections.emptyList())
                    .jobs(Collections.emptyList())
                    .businesses(Collections.emptyList())
                    .services(Collections.emptyList())
                    .marketplace(Collections.emptyList())
                    .food(Collections.emptyList())
                    .documents(Collections.emptyList())
                    .vendors(Collections.emptyList())
                    .build();
        }

        String cleanQuery = query.trim().toLowerCase();
        int limit = (limitPerDomain != null && limitPerDomain > 0) ? limitPerDomain : 6;

        List<GraphVisibility> allowedVisibilities = List.of(GraphVisibility.PUBLIC, GraphVisibility.COMMUNITY);

        // 1. Search relationships
        List<GraphRelationship> matchedRel = relationshipRepository.searchByTargetNameAndVisibilities(
                communityId, RelationshipType.HAS_SKILL, allowedVisibilities, cleanQuery);

        // 2. Search all taxonomies
        List<GraphNodeTaxonomy> matchedTaxonomies = taxonomyRepository.searchActiveTaxonomies(cleanQuery);

        // Classify and bucket results across 10 domains
        List<OmniSearchItemDto> people = new ArrayList<>();
        List<OmniSearchItemDto> events = new ArrayList<>();
        List<OmniSearchItemDto> sports = new ArrayList<>();
        List<OmniSearchItemDto> jobs = new ArrayList<>();
        List<OmniSearchItemDto> businesses = new ArrayList<>();
        List<OmniSearchItemDto> services = new ArrayList<>();
        List<OmniSearchItemDto> marketplace = new ArrayList<>();
        List<OmniSearchItemDto> food = new ArrayList<>();
        List<OmniSearchItemDto> documents = new ArrayList<>();
        List<OmniSearchItemDto> vendors = new ArrayList<>();

        // Populate people from relationships
        for (GraphRelationship rel : matchedRel) {
            if (people.size() >= limit) break;
            people.add(OmniSearchItemDto.builder()
                    .id(rel.getSourceId())
                    .domain(GraphEntityType.USER)
                    .title(rel.getSourceName())
                    .subtitle("Resident • " + rel.getTargetName())
                    .description("Skill: " + rel.getTargetName())
                    .badge("Resident")
                    .deepLink("/community/directory?userId=" + rel.getSourceId())
                    .relevanceScore(computeScore(rel.getSourceName(), rel.getTargetName(), cleanQuery))
                    .tags(List.of(rel.getTargetName()))
                    .build());
        }

        // Map Taxonomies to appropriate domains
        for (GraphNodeTaxonomy tax : matchedTaxonomies) {
            double score = computeScore(tax.getName(), tax.getCode(), cleanQuery);
            OmniSearchItemDto item = OmniSearchItemDto.builder()
                    .id(tax.getId().toString())
                    .domain(tax.getType())
                    .title(tax.getName())
                    .subtitle(tax.getCategoryGroup() != null ? tax.getCategoryGroup() : tax.getType().name())
                    .badge(tax.getType().name())
                    .relevanceScore(score)
                    .tags(List.of(tax.getCode()))
                    .build();

            switch (tax.getType()) {
                case SPORT:
                    if (sports.size() < limit) {
                        item.setDeepLink("/sports/tournaments?sport=" + tax.getCode());
                        sports.add(item);
                    }
                    break;
                case EVENT:
                case CLUB:
                    if (events.size() < limit) {
                        item.setDeepLink("/events?category=" + tax.getCode());
                        events.add(item);
                    }
                    break;
                case PROFESSION:
                    if (jobs.size() < limit) {
                        item.setDeepLink("/network/jobs?role=" + tax.getCode());
                        jobs.add(item);
                    }
                    break;
                case SERVICE:
                    if (services.size() < limit) {
                        item.setDeepLink("/services/home?category=" + tax.getCode());
                        services.add(item);
                    }
                    break;
                case PRODUCT:
                    if (marketplace.size() < limit) {
                        item.setDeepLink("/marketplace?q=" + tax.getName());
                        marketplace.add(item);
                    }
                    break;
                case HOME_CHEF:
                    if (food.size() < limit) {
                        item.setDeepLink("/food/kitchens?tag=" + tax.getCode());
                        food.add(item);
                    }
                    break;
                case VENDOR:
                    if (vendors.size() < limit) {
                        item.setDeepLink("/admin/vendors?type=" + tax.getCode());
                        vendors.add(item);
                    }
                    break;
                default:
                    if (businesses.size() < limit) {
                        item.setDeepLink("/businesses?category=" + tax.getCode());
                        businesses.add(item);
                    }
                    break;
            }
        }

        // Calculate domain counts
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("PEOPLE", (long) people.size());
        counts.put("EVENTS", (long) events.size());
        counts.put("SPORTS", (long) sports.size());
        counts.put("JOBS", (long) jobs.size());
        counts.put("BUSINESSES", (long) businesses.size());
        counts.put("SERVICES", (long) services.size());
        counts.put("MARKETPLACE", (long) marketplace.size());
        counts.put("FOOD", (long) food.size());
        counts.put("DOCUMENTS", (long) documents.size());
        counts.put("VENDORS", (long) vendors.size());

        long total = counts.values().stream().mapToLong(Long::longValue).sum();

        return OmniSearchResponseDto.builder()
                .query(cleanQuery)
                .totalResults(total)
                .domainCounts(counts)
                .people(people)
                .events(events)
                .sports(sports)
                .jobs(jobs)
                .businesses(businesses)
                .services(services)
                .marketplace(marketplace)
                .food(food)
                .documents(documents)
                .vendors(vendors)
                .build();
    }

    private double computeScore(String primary, String secondary, String query) {
        if (primary != null && primary.toLowerCase().equals(query)) return 1.0;
        if (primary != null && primary.toLowerCase().startsWith(query)) return 0.9;
        if (primary != null && primary.toLowerCase().contains(query)) return 0.75;
        if (secondary != null && secondary.toLowerCase().contains(query)) return 0.5;
        return 0.3;
    }
}
