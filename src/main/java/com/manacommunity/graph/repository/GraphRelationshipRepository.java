package com.manacommunity.graph.repository;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.enums.GraphVisibility;
import com.manacommunity.graph.enums.RelationshipType;
import com.manacommunity.graph.model.GraphRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraphRelationshipRepository extends JpaRepository<GraphRelationship, Long> {

    List<GraphRelationship> findByCommunityIdAndSourceTypeAndSourceId(
            Long communityId, GraphEntityType sourceType, String sourceId);

    List<GraphRelationship> findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipType(
            Long communityId, GraphEntityType sourceType, String sourceId, RelationshipType relationshipType);

    List<GraphRelationship> findByCommunityIdAndRelationshipTypeAndTargetTypeAndTargetId(
            Long communityId, RelationshipType relationshipType, GraphEntityType targetType, String targetId);

    List<GraphRelationship> findByCommunityIdAndTargetTypeAndTargetId(
            Long communityId, GraphEntityType targetType, String targetId);

    List<GraphRelationship> findByCommunityIdAndRelationshipType(
            Long communityId, RelationshipType relationshipType);

    Optional<GraphRelationship> findByCommunityIdAndSourceTypeAndSourceIdAndRelationshipTypeAndTargetTypeAndTargetId(
            Long communityId, GraphEntityType sourceType, String sourceId,
            RelationshipType relationshipType, GraphEntityType targetType, String targetId);

    @Modifying
    void deleteByCommunityIdAndSourceTypeAndSourceIdAndRelationshipTypeAndTargetTypeAndTargetId(
            Long communityId, GraphEntityType sourceType, String sourceId,
            RelationshipType relationshipType, GraphEntityType targetType, String targetId);

    @Query("SELECT r.targetName, count(r) FROM GraphRelationship r " +
           "WHERE r.communityId = :communityId AND r.relationshipType = :relType " +
           "GROUP BY r.targetName ORDER BY count(r) DESC")
    List<Object[]> countRelationshipsGroupedByTargetName(
            @Param("communityId") Long communityId, @Param("relType") RelationshipType relType);

    @Query("SELECT count(r) FROM GraphRelationship r " +
           "WHERE r.communityId = :communityId AND r.relationshipType = :relType AND r.targetName = :targetName")
    long countByCommunityIdAndRelationshipTypeAndTargetName(
            @Param("communityId") Long communityId, @Param("relType") RelationshipType relType, @Param("targetName") String targetName);

    @Query("SELECT r FROM GraphRelationship r " +
           "WHERE r.communityId = :communityId AND r.relationshipType = :relType " +
           "AND r.visibility IN :visibilities AND LOWER(r.targetName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<GraphRelationship> searchByTargetNameAndVisibilities(
            @Param("communityId") Long communityId,
            @Param("relType") RelationshipType relType,
            @Param("visibilities") List<GraphVisibility> visibilities,
            @Param("query") String query);

    @Query("SELECT r.targetId, r.targetName, count(r) FROM GraphRelationship r " +
           "WHERE r.communityId = :communityId AND r.relationshipType = :relType AND r.targetType = :targetType " +
           "GROUP BY r.targetId, r.targetName ORDER BY count(r) DESC")
    List<Object[]> findPopularTargets(
            @Param("communityId") Long communityId,
            @Param("relType") RelationshipType relType,
            @Param("targetType") GraphEntityType targetType);
}
