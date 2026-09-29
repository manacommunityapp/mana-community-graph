package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.OmniSearchResponseDto;
import com.manacommunity.graph.service.OmniSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph/omnisearch")
@RequiredArgsConstructor
public class OmniSearchController {

    private final OmniSearchService omniSearchService;

    @GetMapping
    public ResponseEntity<OmniSearchResponseDto> search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "6") Integer limit,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = (principal != null && principal.getAppUser() != null && principal.getAppUser().getCommunity() != null)
                ? principal.getAppUser().getCommunity().getId()
                : 1L;
        Long userId = (principal != null) ? principal.getId() : 1L;

        return ResponseEntity.ok(omniSearchService.performOmniSearch(userId, communityId, q, limit));
    }
}
