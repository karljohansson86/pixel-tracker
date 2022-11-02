package com.karl.pixeltracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karl.pixeltracker.PageVisitReportService;
import com.karl.pixeltracker.PageVisitTrackerService;
import com.karl.pixeltracker.adapter.repository.PostgresPageVisitRepository;
import com.karl.pixeltracker.adapter.repository.PostgresUserRepository;
import com.karl.pixeltracker.adapter.rest.PageVisitController;
import com.karl.pixeltracker.adapter.rest.PageVisitReportController;
import com.karl.pixeltracker.ports.PageVisitReporter;
import com.karl.pixeltracker.ports.PageVisitTracker;
import com.karl.pixeltracker.ports.repository.PageVisitRecordsRepository;
import com.karl.pixeltracker.ports.repository.PageVisitRepository;
import com.karl.pixeltracker.ports.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PixelTrackerConfig {

    @Bean
    public PageVisitController pageVisitController(PageVisitTracker pageVisitTracker) {
        return new PageVisitController(pageVisitTracker);
    }

    @Bean
    public PageVisitReportController pageVisitReportController(PageVisitReporter pageVisitReporter) {
        return new PageVisitReportController(pageVisitReporter);
    }

    @Bean
    public PageVisitTracker pageVisitTrackerService(UserRepository userRepository, PageVisitRepository pageVisitRepository) {
        return new PageVisitTrackerService(userRepository, pageVisitRepository);
    }

    @Bean
    public PageVisitReporter pageVisitReporter(PageVisitRecordsRepository pageVisitRecordsRepository) {
        return new PageVisitReportService(pageVisitRecordsRepository);
    }

    @Bean
    public UserRepository userRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new PostgresUserRepository(jdbcTemplate);
    }

    @Bean
    public PostgresPageVisitRepository visitorRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new PostgresPageVisitRepository(jdbcTemplate);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    @Bean
    public static ObjectMapper getObjectMapper() {
        return ObjectMapperFactory.create();
    }
}
