package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.GraphRelationshipRequest;
import com.manacommunity.graph.dto.GraphRelationshipResponse;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import com.manacommunity.graph.repository.GraphRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphRelationshipService {

    private final GraphRelationshipRepository relationshipRepository;

    @Transactional
    public GraphRelationshipResponse addOrUpdateRelationship(Long communityId, GraphRelationshipRequest req) {
        Optional<GraphRelationship> existing = relationshipRepository
                .findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipTypeAndTargetTypeAndTargetId(
                        communityId, req.getSourceType(), req.getSourceId(),
                        req.getRelationshipType(), req.getTargetType(), req.getTargetId());

        GraphRelationship relationship;
        if (existing.isPresent()) {
            relationship = existing.get();
            if (req.getSourceName() != null) relationship.setSourceName(req.getSourceName());
            if (req.getTargetName() != null) relationship.setTargetName(req.getTargetName());
            if (req.getVisibility() != null) relationship.setVisibility(req.getVisibility());
            if (req.getConfidence() != null) relationship.setConfidence(req.getConfidence());
            if (req.getAffinityWeight() != null) relationship.setAffinityWeight(req.getAffinityWeight());
            if (req.getSourceSystem() != null) relationship.setSourceSystem(req.getSourceSystem());
            if (req.getMetadataJson() != null) relationship.setMetadataJson(req.getMetadataJson());
        } else {
            relationship = GraphRelationship.builder()
                    .communityId(communityId)
                    .sourceType(req.getSourceType())
                    .sourceId(req.getSourceId())
                    .sourceName(req.getSourceName())
                    .relationshipType(req.getRelationshipType())
                    .targetType(req.getTargetType())
                    .targetId(req.getTargetId())
                    .targetName(req.getTargetName())
                    .visibility(req.getVisibility() != null ? req.getVisibility() : GraphVisibility.COMMUNITY)
                    .confidence(req.getConfidence() != null ? req.getConfidence() : 100)
                    .affinityWeight(req.getAffinityWeight() != null ? req.getAffinityWeight() : 1.0)
                    .sourceSystem(req.getSourceSystem())
                    .metadataJson(req.getMetadataJson())
                    .build();
        }

        GraphRelationship saved = relationshipRepository.save(relationship);
        log.info("Graph Edge saved: ({} : {}) -[{}]-> ({} : {})",
                saved.getSourceType(), saved.getSourceName(), saved.getRelationshipType(), saved.getTargetType(), saved.getTargetName());
        return mapToResponse(saved);
    }

    public List<GraphRelationshipResponse> getOutgoingRelationships(Long communityId, GraphEntityType sourceType, String sourceId) {
        return relationshipRepository.findByCommunityIdAndSourceTypeAndSourceId(communityId, sourceType, sourceId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<GraphRelationshipResponse> getIncomingRelationships(Long communityId, GraphEntityType targetType, String targetId) {
        return relationshipRepository.findByCommunityIdAndTargetTypeAndTargetId(communityId, targetType, targetId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<GraphRelationshipResponse> getRelationshipsByType(Long communityId, RelationshipType relType) {
        return relationshipRepository.findByCommunityIdAndRelationshipType(communityId, relType)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void removeRelationship(Long communityId, GraphEntityType sourceType, String sourceId,
                                   RelationshipType relType, GraphEntityType targetType, String targetId) {
        relationshipRepository.deleteByCommunityIdAndSourceTypeAndSourceIdAndRelationshipTypeAndTargetTypeAndTargetId(
                communityId, sourceType, sourceId, relType, targetType, targetId);
    }

    public GraphRelationshipResponse mapToResponse(GraphRelationship r) {
        return GraphRelationshipResponse.builder()
                .id(r.getId())
                .communityId(r.getCommunityId())
                .sourceType(r.getSourceType())
                .sourceId(r.getSourceId())
                .sourceName(r.getSourceName())
                .relationshipType(r.getRelationshipType())
                .targetType(r.getTargetType())
                .targetId(r.getTargetId())
                .targetName(r.getTargetName())
                .visibility(r.getVisibility())
                .confidence(r.getConfidence())
                .affinityWeight(r.getAffinityWeight())
                .sourceSystem(r.getSourceSystem())
                .metadataJson(r.getMetadataJson())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
