package com.manacommunity.graph.model;

import com.manacommunity.graph.enums.GraphEntityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    schema = "manacommunity",
    name = "graph_node_taxonomies",
    uniqueConstraints = @UniqueConstraint(columnNames = {"type", "code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphNodeTaxonomy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long communityId; // Null for system global taxonomies

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GraphEntityType type; // SKILL, PROFESSION, SPORT, CLUB, SERVICE, etc.

    @Column(nullable = false, length = 100)
    private String code; // e.g. "JAVA", "INTERIOR_DESIGN", "CRICKET"

    @Column(nullable = false)
    private String name; // e.g. "Java", "Interior Design", "Cricket"

    private String categoryGroup; // e.g. "Technology", "Home & Living", "Outdoor Sports"
    private String icon;

    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
