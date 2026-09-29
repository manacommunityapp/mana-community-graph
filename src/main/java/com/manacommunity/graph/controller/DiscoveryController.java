package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.CommunityTrendDto;
import com.manacommunity.graph.dto.SmartSearchResultDto;
import com.manacommunity.graph.service.CommunityDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph/discover")
@RequiredArgsConstructor
public class DiscoveryController {

    private final CommunityDiscoveryService discoveryService;

    @GetMapping("/search")
    public ResponseEntity<SmartSearchResultDto> searchCommunity(
            @RequestParam String q,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(discoveryService.searchCommunity(principal.getId(), communityId, q));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<CommunityTrendDto>> getTopSkills(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(discoveryService.getTopSkills(communityId));
    }
}
