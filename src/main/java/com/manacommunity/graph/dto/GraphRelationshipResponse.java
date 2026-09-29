package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphRelationshipResponse {
    private Long id;
    private Long communityId;
    private GraphEntityType sourceType;
    private String sourceId;
    private String sourceName;
    private RelationshipType relationshipType;
    private GraphEntityType targetType;
    private String targetId;
    private String targetName;
    private GraphVisibility visibility;
    private Integer confidence;
    private Double affinityWeight;
    private String sourceSystem;
    private String metadataJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
