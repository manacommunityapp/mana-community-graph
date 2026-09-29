package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphEventType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphIngestEventRequest {
    @NotNull(message = "eventType is required")
    private GraphEventType eventType;

    private String sourceSystem;
    private String userId;
    private String userName;
    private String targetId;
    private String targetName;
    private String secondaryTargetId;
    private String secondaryTargetName;
    private Map<String, Object> attributes;
}
