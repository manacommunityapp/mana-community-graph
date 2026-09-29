package com.manacommunity.graph.controller;

import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.graph.dto.UserProfileVisibilityRequest;
import com.manacommunity.graph.dto.UserProfileVisibilityResponse;
import com.manacommunity.graph.service.PrivacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph/privacy")
@RequiredArgsConstructor
public class PrivacyController {

    private final PrivacyService privacyService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileVisibilityResponse> getMyVisibility(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(privacyService.getVisibilityResponse(principal.getId(), communityId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileVisibilityResponse> updateMyVisibility(
            @RequestBody UserProfileVisibilityRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long communityId = principal.getAppUser().getCommunity().getId();
        return ResponseEntity.ok(privacyService.updateVisibility(principal.getId(), communityId, req));
    }
}
