package com.manacommunity.graph.dto;

import com.manacommunity.graph.enums.GraphEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphNodeTaxonomyDto {
    private Long id;
    private GraphEntityType type;
    private String code;
    private String name;
    private String categoryGroup;
    private String icon;
    private Boolean active;
}
