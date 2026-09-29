package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.GraphIngestEventRequest;
import com.manacommunity.graph.dto.GraphRelationshipRequest;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphIngestionLog;
import com.manacommunity.graph.repository.GraphIngestionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphIngestionService {

    private final GraphRelationshipService relationshipService;
    private final GraphIngestionLogRepository ingestionLogRepository;

    @Transactional
    public void ingestEvent(Long communityId, GraphIngestEventRequest event) {
        log.info("Processing Graph Ingestion Event: type={}, user={}, target={}",
                event.getEventType(), event.getUserName(), event.getTargetName());

        switch (event.getEventType()) {
            case USER_SKILL_ADDED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.HAS_SKILL)
                        .targetType(GraphEntityType.SKILL)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.COMMUNITY)
                        .sourceSystem(event.getSourceSystem() != null ? event.getSourceSystem() : "PROFILE_SERVICE")
                        .build());
                break;

            case USER_SKILL_REMOVED:
                relationshipService.removeRelationship(communityId, GraphEntityType.USER, event.getUserId(),
                        RelationshipType.HAS_SKILL, GraphEntityType.SKILL, event.getTargetId());
                break;

            case USER_INTEREST_ADDED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.INTERESTED_IN)
                        .targetType(GraphEntityType.CLUB)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.COMMUNITY)
                        .sourceSystem(event.getSourceSystem())
                        .build());
                break;

            case SPORT_TEAM_JOINED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.MEMBER_OF)
                        .targetType(GraphEntityType.SPORT)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.COMMUNITY)
                        .sourceSystem("SPORTS_SERVICE")
                        .build());
                break;

            case EVENT_REGISTERED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.REGISTERED_FOR)
                        .targetType(GraphEntityType.EVENT)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.PRIVATE)
                        .sourceSystem("EVENTS_SERVICE")
                        .build());
                break;

            case TRIP_JOINED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.BOOKED)
                        .targetType(GraphEntityType.TRIP)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.PRIVATE)
                        .sourceSystem("TRAVEL_SERVICE")
                        .build());
                break;

            case SERVICE_OFFER_CREATED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.OFFERS_SERVICE)
                        .targetType(GraphEntityType.SERVICE)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.COMMUNITY)
                        .sourceSystem("SERVICES_PLATFORM")
                        .build());
                break;

            case GROUP_BUY_JOINED:
                relationshipService.addOrUpdateRelationship(communityId, GraphRelationshipRequest.builder()
                        .sourceType(GraphEntityType.USER)
                        .sourceId(event.getUserId())
                        .sourceName(event.getUserName())
                        .relationshipType(RelationshipType.BOUGHT)
                        .targetType(GraphEntityType.GROUP_BUY)
                        .targetId(event.getTargetId())
                        .targetName(event.getTargetName())
                        .visibility(GraphVisibility.SYSTEM_ONLY)
                        .sourceSystem("GROUP_MARKETPLACE")
                        .build());
                break;

            default:
                log.warn("Unhandled Graph Event: {}", event.getEventType());
        }

        // Record audit log
        ingestionLogRepository.save(GraphIngestionLog.builder()
                .communityId(communityId)
                .eventType(event.getEventType())
                .sourceSystem(event.getSourceSystem())
                .payloadJson(event.toString())
                .status("SUCCESS")
                .build());
    }
}
