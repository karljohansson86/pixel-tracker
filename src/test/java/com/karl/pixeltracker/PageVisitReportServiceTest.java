package com.karl.pixeltracker;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.Report;
import com.karl.pixeltracker.domain.report.TimeRange;
import com.karl.pixeltracker.ports.repository.PageVisitRecordsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageVisitReportServiceTest {

    private PageVisitRecordsRepository repository;
    private PageVisitReportService service;

    @BeforeEach
    void setUp() {
        repository = mock(PageVisitRecordsRepository.class);
        service = new PageVisitReportService(repository);
    }

    @Test
    void should_create_report() {
        String expectedUrl = "/contact.html";
        int expectedNoPageViews = 5;
        int expectedNoUniqueVisitors = 1;
        PageVisitRecord expected = new PageVisitRecord(expectedUrl, expectedNoPageViews, expectedNoUniqueVisitors);
        OffsetDateTime expectedStartTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        OffsetDateTime expectedEndTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        TimeRange timeRange = new TimeRange(expectedStartTimestamp, expectedEndTimestamp);

        when(repository.getPageVisitRecordsForTimeRange(timeRange)).thenReturn(List.of(expected));

        Report actual = service.createReportForTimeRange(timeRange);

        assertThat(actual.records()).isNotEmpty();
        PageVisitRecord actualPageVisitRecord = actual.records().get(0);
        assertThat(actualPageVisitRecord.pageViews()).isEqualTo(expectedNoPageViews);
        assertThat(actualPageVisitRecord.visitors()).isEqualTo(expectedNoUniqueVisitors);
        assertThat(actualPageVisitRecord.url()).isEqualTo(expectedUrl);
    }
}