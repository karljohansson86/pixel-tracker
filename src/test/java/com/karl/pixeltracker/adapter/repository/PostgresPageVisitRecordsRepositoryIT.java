package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.TestPostgresContainer;
import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.TimeRange;
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
public class PostgresPageVisitRecordsRepositoryIT {

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
        testDbUtils.clearTables();
    }

    @Test
    void should_get_report_from_given_time_range() {
        UUID expectedUserId = UUID.fromString("c8cfa141-b3e9-45e0-b1d6-8f23c99f32e3");
        testDbUtils.insertUser(expectedUserId);
        String expectedUrl = "/contact.html";
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(expectedUserId, expectedUrl, expectedTimestamp);
        int expectedNoPageViews = 5;
        int expectedNoUniqueVisitors = 1;

        testDbUtils.insertMultiplePageVisits(pageVisit, expectedNoPageViews);

        OffsetDateTime start = expectedTimestamp.minusHours(1L);
        OffsetDateTime end = expectedTimestamp.plusHours(1L);
        TimeRange timeRange = new TimeRange(start, end);

        List<PageVisitRecord> actual = repository.getPageVisitRecordsForTimeRange(timeRange);

        assertThat(actual).isNotEmpty();
        PageVisitRecord actualPageVisitRecord = actual.get(0);
        assertThat(actualPageVisitRecord.pageViews()).isEqualTo(expectedNoPageViews);
        assertThat(actualPageVisitRecord.visitors()).isEqualTo(expectedNoUniqueVisitors);
        assertThat(actualPageVisitRecord.url()).isEqualTo(expectedUrl);
    }

    @Test
    void should_ignore_page_visits_outside_of_given_time_range() {
        UUID expectedUserId = UUID.fromString("e6e47f14-4ac0-4e38-9644-a62654146c7d");
        testDbUtils.insertUser(expectedUserId);
        String expectedUrl = "/contact.html";
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(expectedUserId, expectedUrl, expectedTimestamp);

        testDbUtils.insertPageVisit(pageVisit);

        OffsetDateTime start = expectedTimestamp.minusDays(2L);
        OffsetDateTime end = expectedTimestamp.minusDays(1L);
        TimeRange timeRange = new TimeRange(start, end);

        List<PageVisitRecord> actual = repository.getPageVisitRecordsForTimeRange(timeRange);

        assertThat(actual).isEmpty();
    }
}
