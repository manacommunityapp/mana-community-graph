package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVisibilityRequest {
    private GraphVisibility professionVisibility;
    private GraphVisibility skillsVisibility;
    private GraphVisibility interestsVisibility;
    private GraphVisibility sportsVisibility;
    private GraphVisibility servicesVisibility;
    private GraphVisibility eventsVisibility;
    private GraphVisibility tripsVisibility;
    private GraphVisibility purchasesVisibility;
    private GraphVisibility flatVisibility;
}
