package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.GraphRelationshipRequest;
import com.manacommunity.graph.dto.GraphRelationshipResponse;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.service.GraphRelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph/relationships")
@RequiredArgsConstructor
public class GraphRelationshipController {

    private final GraphRelationshipService relationshipService;

    @PostMapping
    public ResponseEntity<GraphRelationshipResponse> createRelationship(
            @Valid @RequestBody GraphRelationshipRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(relationshipService.addOrUpdateRelationship(communityId, req));
    }

    @GetMapping("/source/{sourceType}/{sourceId}")
    public ResponseEntity<List<GraphRelationshipResponse>> getOutgoing(
            @PathVariable GraphEntityType sourceType,
            @PathVariable String sourceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(relationshipService.getOutgoingRelationships(communityId, sourceType, sourceId));
    }

    @GetMapping("/target/{targetType}/{targetId}")
    public ResponseEntity<List<GraphRelationshipResponse>> getIncoming(
            @PathVariable GraphEntityType targetType,
            @PathVariable String targetId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(relationshipService.getIncomingRelationships(communityId, targetType, targetId));
    }

    @GetMapping("/type/{relType}")
    public ResponseEntity<List<GraphRelationshipResponse>> getByType(
            @PathVariable RelationshipType relType,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(relationshipService.getRelationshipsByType(communityId, relType));
    }
}
