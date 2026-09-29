package com.manacommunity.graph.model;

import com.manacommunity.graph.enums.GraphVisibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    schema = "manacommunity",
    name = "user_profile_visibilities",
    uniqueConstraints = @UniqueConstraint(columnNames = {"communityId", "userId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility professionVisibility = GraphVisibility.COMMUNITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility skillsVisibility = GraphVisibility.COMMUNITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility interestsVisibility = GraphVisibility.COMMUNITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility sportsVisibility = GraphVisibility.COMMUNITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility servicesVisibility = GraphVisibility.COMMUNITY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility eventsVisibility = GraphVisibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility tripsVisibility = GraphVisibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility purchasesVisibility = GraphVisibility.SYSTEM_ONLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility flatVisibility = GraphVisibility.COMMUNITY;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
