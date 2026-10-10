package com.manacommunity.graph.config;

import com.manacommunity.common.util.SecurityErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityErrorWriterConfig {

    @Bean
    public SecurityErrorWriter securityErrorWriter() {
        return new SecurityErrorWriter();
    }
}