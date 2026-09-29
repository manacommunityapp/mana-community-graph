package com.manacommunity.graph.repository;

import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.model.GraphNodeTaxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraphNodeTaxonomyRepository extends JpaRepository<GraphNodeTaxonomy, Long> {
    List<GraphNodeTaxonomy> findByTypeAndActiveTrue(GraphEntityType type);
    List<GraphNodeTaxonomy> findByTypeAndCategoryGroupAndActiveTrue(GraphEntityType type, String categoryGroup);
    Optional<GraphNodeTaxonomy> findByTypeAndCode(GraphEntityType type, String code);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM GraphNodeTaxonomy t WHERE t.active = true AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<GraphNodeTaxonomy> searchActiveTaxonomies(@org.springframework.data.repository.query.Param("query") String query);
}
