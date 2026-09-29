package com.manacommunity.graph.model;

import com.manacommunity.graph.enums.GraphEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(schema = "manacommunity", name = "graph_ingestion_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphIngestionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GraphEventType eventType;

    private String sourceSystem;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Builder.Default
    private String status = "PROCESSED";

    @CreationTimestamp
    private LocalDateTime createdAt;
}
