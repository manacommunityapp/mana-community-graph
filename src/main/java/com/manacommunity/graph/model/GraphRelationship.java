package com.manacommunity.graph.model;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    schema = "manacommunity",
    name = "community_graph_relationships",
    indexes = {
        @Index(name = "idx_cgr_comm_src", columnList = "communityId, sourceType, sourceId"),
        @Index(name = "idx_cgr_comm_tgt", columnList = "communityId, targetType, targetId"),
        @Index(name = "idx_cgr_comm_rel", columnList = "communityId, relationshipType"),
        @Index(name = "idx_cgr_comm_src_rel", columnList = "communityId, sourceType, sourceId, relationshipType")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GraphEntityType sourceType;

    @Column(nullable = false, length = 100)
    private String sourceId;

    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RelationshipType relationshipType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GraphEntityType targetType;

    @Column(nullable = false, length = 100)
    private String targetId;

    private String targetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GraphVisibility visibility = GraphVisibility.COMMUNITY;

    @Builder.Default
    private Integer confidence = 100; // 0-100

    @Builder.Default
    private Double affinityWeight = 1.0;

    private String sourceSystem;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
