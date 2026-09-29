package com.manacommunity.graph.controller;

import com.manacommunity.graph.dto.GraphNodeTaxonomyDto;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.service.TaxonomyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph/taxonomy")
@RequiredArgsConstructor
public class TaxonomyController {

    private final TaxonomyService taxonomyService;

    @GetMapping("/type/{type}")
    public ResponseEntity<List<GraphNodeTaxonomyDto>> getTaxonomiesByType(
            @PathVariable GraphEntityType type) {
        return ResponseEntity.ok(taxonomyService.getTaxonomiesByType(type));
    }

    @PostMapping
    public ResponseEntity<GraphNodeTaxonomyDto> createTaxonomy(
            @RequestBody GraphNodeTaxonomyDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taxonomyService.createTaxonomy(req));
    }
}
