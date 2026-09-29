package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphRelationshipRequest {
    @NotNull(message = "sourceType is required")
    private GraphEntityType sourceType;

    @NotBlank(message = "sourceId is required")
    private String sourceId;

    private String sourceName;

    @NotNull(message = "relationshipType is required")
    private RelationshipType relationshipType;

    @NotNull(message = "targetType is required")
    private GraphEntityType targetType;

    @NotBlank(message = "targetId is required")
    private String targetId;

    private String targetName;

    private GraphVisibility visibility;
    private Integer confidence;
    private Double affinityWeight;
    private String sourceSystem;
    private String metadataJson;
}
