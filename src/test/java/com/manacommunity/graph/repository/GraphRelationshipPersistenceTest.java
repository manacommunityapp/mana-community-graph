package com.manacommunity.graph.repository;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GraphRelationshipPersistenceTest {

    @Autowired
    private GraphRelationshipRepository relationshipRepository;

    @Test
    @DisplayName("Verify actual database persistence and dynamic aggregation queries")
    void testPersistenceAndAggregationQueries() {
        Long communityId = 99L;

        GraphRelationship reg1 = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("user-1")
                .sourceName("Alice")
                .relationshipType(RelationshipType.REGISTERED_FOR)
                .targetType(GraphEntityType.EVENT)
                .targetId("EVT-Diwali")
                .targetName("Diwali Celebration")
                .visibility(GraphVisibility.COMMUNITY)
                .build();

        GraphRelationship reg2 = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("user-2")
                .sourceName("Bob")
                .relationshipType(RelationshipType.REGISTERED_FOR)
                .targetType(GraphEntityType.EVENT)
                .targetId("EVT-Diwali")
                .targetName("Diwali Celebration")
                .visibility(GraphVisibility.COMMUNITY)
                .build();

        GraphRelationship reg3 = GraphRelationship.builder()
                .communityId(communityId)
                .sourceType(GraphEntityType.USER)
                .sourceId("user-3")
                .sourceName("Charlie")
                .relationshipType(RelationshipType.REGISTERED_FOR)
                .targetType(GraphEntityType.EVENT)
                .targetId("EVT-Yoga")
                .targetName("Morning Yoga")
                .visibility(GraphVisibility.COMMUNITY)
                .build();

        relationshipRepository.saveAll(List.of(reg1, reg2, reg3));
        relationshipRepository.flush();

        List<Object[]> popularEvents = relationshipRepository.findPopularTargets(
                communityId, RelationshipType.REGISTERED_FOR, GraphEntityType.EVENT);

        assertNotNull(popularEvents);
        assertEquals(2, popularEvents.size());

        // Top event should be Diwali Celebration with 2 registrations
        assertEquals("EVT-Diwali", popularEvents.get(0)[0]);
        assertEquals("Diwali Celebration", popularEvents.get(0)[1]);
        assertEquals(2L, ((Number) popularEvents.get(0)[2]).longValue());

        // Second event should be Morning Yoga with 1 registration
        assertEquals("EVT-Yoga", popularEvents.get(1)[0]);
        assertEquals("Morning Yoga", popularEvents.get(1)[1]);
        assertEquals(1L, ((Number) popularEvents.get(1)[2]).longValue());
    }
}
