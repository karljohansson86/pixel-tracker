package com.karl.pixeltracker.adapter.rest;

import com.karl.pixeltracker.TestPostgresContainer;
import com.karl.pixeltracker.adapter.repository.TestDbUtils;
import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PageVisitReportControllerIT {

    private static final String EXPECTED_URL = "/contact.html";
    private static final String REFERER_HEADER = "referer";

    private static final PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private TestDbUtils testDbUtils;

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        testDbUtils = new TestDbUtils(jdbcTemplate);
        testDbUtils.clearTables();
    }

    @Test
    void should_get_report_from_time_range() {
        UUID expectedUserId = UUID.fromString("0a3083e0-0879-4230-b44b-3e674c6877be");
        insertOneUserAndPageVisit(expectedUserId);

        int expectedNoPageViews = 1;
        int expectedNoUniqueVisitors = 1;
        OffsetDateTime expectedStartTimestamp = OffsetDateTime.now(ZoneId.of("UTC")).minusHours(1);
        OffsetDateTime expectedEndTimestamp = OffsetDateTime.now(ZoneId.of("UTC")).plusHours(1);

        WebTestClient.ResponseSpec actual = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/page-visit/report")
                        .queryParam("start", expectedStartTimestamp)
                        .queryParam("end", expectedEndTimestamp)
                        .build())
                .exchange();

        actual
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.pageVisits[0].url").isEqualTo(EXPECTED_URL)
                .jsonPath("$.pageVisits[0].pageViews").isEqualTo(expectedNoPageViews)
                .jsonPath("$.pageVisits[0].visitors").isEqualTo(expectedNoUniqueVisitors);
    }

    @Test
    void should_get_report_for_last_day() {
        UUID expectedUserId = UUID.fromString("ac11a60d-8770-44f5-a152-25ab525f5e1b");
        insertOneUserAndPageVisit(expectedUserId);

        int expectedNoPageViews = 1;
        int expectedNoUniqueVisitors = 1;

        WebTestClient.ResponseSpec actual = webTestClient
                .get()
                .uri("/v1/page-visit/report/last-day")
                .exchange();

        actual
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.pageVisits[0].url").isEqualTo(EXPECTED_URL)
                .jsonPath("$.pageVisits[0].pageViews").isEqualTo(expectedNoPageViews)
                .jsonPath("$.pageVisits[0].visitors").isEqualTo(expectedNoUniqueVisitors);
    }

    private void insertOneUserAndPageVisit(UUID expectedUserId) {
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(expectedUserId, EXPECTED_URL, expectedTimestamp);

        testDbUtils.insertUser(expectedUserId);
        testDbUtils.insertPageVisit(pageVisit);
    }
}
