package com.manacommunity.graph.repository;

import com.manacommunity.graph.model.GraphIngestionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphIngestionLogRepository extends JpaRepository<GraphIngestionLog, Long> {
    Page<GraphIngestionLog> findByCommunityIdOrderByCreatedAtDesc(Long communityId, Pageable pageable);
}
