package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.TestPostgresContainer;
import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostgresPageVisitRepositoryIT {

    private static final String EXPECTED_URL = "/contact.html";

    private static final PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private PostgresPageVisitRepository repository;
    private TestDbUtils testDbUtils;


    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        repository = new PostgresPageVisitRepository(jdbcTemplate);
        testDbUtils = new TestDbUtils(jdbcTemplate);
    }

    @Test
    void should_register_visit() {
        UUID expectedUserId = UUID.fromString("4379c8d1-870d-48f1-9117-5e6dbcc12ea6");
        testDbUtils.insertUser(expectedUserId);

        User expectedUser = new User(expectedUserId);
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(expectedUserId, EXPECTED_URL, expectedTimestamp);

        repository.register(expectedUser, pageVisit);

        List<PageVisit> actual = testDbUtils.getPageVisitsForUserId(expectedUserId);

        assertThat(actual.size()).isEqualTo(1);
        PageVisit actualPageVisit = actual.get(0);

        assertThat(actualPageVisit.userId()).isEqualTo(expectedUserId);
        assertThat(actualPageVisit.url()).isEqualTo(EXPECTED_URL);
        assertThat(actualPageVisit.timestamp()).isEqualTo(expectedTimestamp);
    }

    @Test
    void should_register_recurring_visit() {
        UUID expectedUserId = UUID.fromString("1545f786-c0af-49e3-9d97-64a41820cdb5");
        User expectedUser = new User(expectedUserId);
        testDbUtils.insertUser(expectedUserId);

        OffsetDateTime timestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit firstPageVisit = new PageVisit(expectedUserId, EXPECTED_URL, timestamp);

        // First time visit
        repository.register(expectedUser, firstPageVisit);
        List<PageVisit> pageVisitsForUserId = testDbUtils.getPageVisitsForUserId(expectedUserId);
        assertThat(pageVisitsForUserId.size()).isEqualTo(1);

        // Second time visit
        timestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit secondPageVisit = new PageVisit(expectedUserId, EXPECTED_URL, timestamp);
        repository.register(expectedUser, secondPageVisit);
        pageVisitsForUserId = testDbUtils.getPageVisitsForUserId(expectedUserId);
        assertThat(pageVisitsForUserId.size()).isEqualTo(2);
    }

}