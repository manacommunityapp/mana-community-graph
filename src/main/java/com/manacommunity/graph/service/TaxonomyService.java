package com.manacommunity.graph.service;

import com.manacommunity.graph.dto.GraphNodeTaxonomyDto;
import com.manacommunity.graph.enums.GraphEntityType;
import com.manacommunity.graph.model.GraphNodeTaxonomy;
import com.manacommunity.graph.repository.GraphNodeTaxonomyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxonomyService {

    private final GraphNodeTaxonomyRepository taxonomyRepository;

    @PostConstruct
    public void initDefaultTaxonomies() {
        if (taxonomyRepository.count() == 0) {
            log.info("Seeding initial Graph Node Taxonomies...");
            // Skills
            seedTaxonomy(GraphEntityType.SKILL, "JAVA", "Java", "Technology", "💻");
            seedTaxonomy(GraphEntityType.SKILL, "SPRING_BOOT", "Spring Boot", "Technology", "🌱");
            seedTaxonomy(GraphEntityType.SKILL, "AWS", "AWS", "Technology", "☁️");
            seedTaxonomy(GraphEntityType.SKILL, "REACT", "React", "Technology", "⚛️");
            seedTaxonomy(GraphEntityType.SKILL, "INTERIOR_DESIGN", "Interior Design", "Home & Living", "🏠");
            seedTaxonomy(GraphEntityType.SKILL, "PHOTOGRAPHY", "Photography", "Creative Arts", "📷");
            seedTaxonomy(GraphEntityType.SKILL, "GARDENING", "Gardening", "Home & Living", "🌱");
            seedTaxonomy(GraphEntityType.SKILL, "YOGA_INSTRUCTOR", "Yoga Instruction", "Wellness", "🧘");
            seedTaxonomy(GraphEntityType.SKILL, "BAKING", "Baking & Pastry", "Food & Culinary", "🧁");

            // Professions
            seedTaxonomy(GraphEntityType.PROFESSION, "SOFTWARE_ENGINEER", "Software Engineer", "Technology", "💻");
            seedTaxonomy(GraphEntityType.PROFESSION, "DOCTOR", "Doctor / Healthcare", "Medical", "🩺");
            seedTaxonomy(GraphEntityType.PROFESSION, "ADVOCATE", "Advocate / Legal", "Legal", "⚖️");
            seedTaxonomy(GraphEntityType.PROFESSION, "INTERIOR_DESIGNER", "Interior Designer", "Design", "📐");
            seedTaxonomy(GraphEntityType.PROFESSION, "CHARTERED_ACCOUNTANT", "Chartered Accountant", "Finance", "🏦");
            seedTaxonomy(GraphEntityType.PROFESSION, "TEACHER", "Teacher / Educator", "Education", "📚");

            // Sports
            seedTaxonomy(GraphEntityType.SPORT, "CRICKET", "Cricket", "Outdoor Sports", "🏏");
            seedTaxonomy(GraphEntityType.SPORT, "BADMINTON", "Badminton", "Racquet Sports", "🏸");
            seedTaxonomy(GraphEntityType.SPORT, "CYCLING", "Cycling", "Fitness", "🚴");
            seedTaxonomy(GraphEntityType.SPORT, "FOOTBALL", "Football", "Outdoor Sports", "⚽");
            seedTaxonomy(GraphEntityType.SPORT, "SWIMMING", "Swimming", "Aquatics", "🏊");
            seedTaxonomy(GraphEntityType.SPORT, "TABLE_TENNIS", "Table Tennis", "Indoor Sports", "🏓");

            // Clubs
            seedTaxonomy(GraphEntityType.CLUB, "FOOD_CLUB", "Food & Culinary Club", "Interest Clubs", "🍱");
            seedTaxonomy(GraphEntityType.CLUB, "TREKKING_CLUB", "Trekking & Adventure Club", "Interest Clubs", "🥾");
            seedTaxonomy(GraphEntityType.CLUB, "BOOK_CLUB", "Book Reading Club", "Interest Clubs", "📖");
        }
    }

    private void seedTaxonomy(GraphEntityType type, String code, String name, String group, String icon) {
        taxonomyRepository.save(GraphNodeTaxonomy.builder()
                .type(type)
                .code(code)
                .name(name)
                .categoryGroup(group)
                .icon(icon)
                .active(true)
                .build());
    }

    public List<GraphNodeTaxonomyDto> getTaxonomiesByType(GraphEntityType type) {
        return taxonomyRepository.findByTypeAndActiveTrue(type)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public GraphNodeTaxonomyDto createTaxonomy(GraphNodeTaxonomyDto dto) {
        GraphNodeTaxonomy node = GraphNodeTaxonomy.builder()
                .type(dto.getType())
                .code(dto.getCode().toUpperCase().replace(" ", "_"))
                .name(dto.getName())
                .categoryGroup(dto.getCategoryGroup())
                .icon(dto.getIcon())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return mapToDto(taxonomyRepository.save(node));
    }

    public GraphNodeTaxonomyDto mapToDto(GraphNodeTaxonomy n) {
        return GraphNodeTaxonomyDto.builder()
                .id(n.getId())
                .type(n.getType())
                .code(n.getCode())
                .name(n.getName())
                .categoryGroup(n.getCategoryGroup())
                .icon(n.getIcon())
                .active(n.getActive())
                .build();
    }
}
