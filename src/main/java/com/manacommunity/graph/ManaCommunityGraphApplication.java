package com.manacommunity.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.manacommunity.graph",
        "com.manacommunity.common.security"
})
@EntityScan(basePackages = {
        "com.manacommunity.graph.model",
        "com.manacommunity.common.model",
        "com.manacommunity.common.user.model"
})
@EnableJpaRepositories(basePackages = {
        "com.manacommunity.graph.repository",
        "com.manacommunity.common.repository",
        "com.manacommunity.common.user.repository"
})
public class ManaCommunityGraphApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManaCommunityGraphApplication.class, args);
    }
}
