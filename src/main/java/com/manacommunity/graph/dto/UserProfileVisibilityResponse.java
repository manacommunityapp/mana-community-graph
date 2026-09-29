package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVisibilityResponse {
    private Long id;
    private Long communityId;
    private Long userId;
    private GraphVisibility professionVisibility;
    private GraphVisibility skillsVisibility;
    private GraphVisibility interestsVisibility;
    private GraphVisibility sportsVisibility;
    private GraphVisibility servicesVisibility;
    private GraphVisibility eventsVisibility;
    private GraphVisibility tripsVisibility;
    private GraphVisibility purchasesVisibility;
    private GraphVisibility flatVisibility;
    private LocalDateTime updatedAt;
}
