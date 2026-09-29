package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.GraphIngestEventRequest;
import com.manacommunity.graph.service.GraphIngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/graph/ingest")
@RequiredArgsConstructor
public class GraphIngestionController {

    private final GraphIngestionService ingestionService;

    @PostMapping("/event")
    public ResponseEntity<Map<String, String>> ingestEvent(
            @Valid @RequestBody GraphIngestEventRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        ingestionService.ingestEvent(communityId, req);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Event projected to graph"));
    }
}
