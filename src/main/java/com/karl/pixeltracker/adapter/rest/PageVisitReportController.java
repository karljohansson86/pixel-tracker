package com.karl.pixeltracker.adapter.rest;

import com.karl.pixeltracker.adapter.rest.dto.ReportDto;
import com.karl.pixeltracker.domain.report.Report;
import com.karl.pixeltracker.domain.report.TimeRange;
import com.karl.pixeltracker.ports.PageVisitReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
@ResponseBody
@RequestMapping("/v1")
public class PageVisitReportController {
    private final PageVisitReporter pageVisitReportService;

    public PageVisitReportController(PageVisitReporter pageVisitReportService) {
        this.pageVisitReportService = pageVisitReportService;
    }

    @GetMapping(value = "/page-visit/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReportDto getReport(@RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
                               @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        log.info("Request for report, start {}, end {} ", start, end);

        TimeRange timeRange = new TimeRange(start, end);

        Report report = pageVisitReportService.createReportForTimeRange(timeRange);

        return ReportDto.fromDomain(report);
    }

    @GetMapping(value = "/page-visit/report/last-day", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReportDto getReport() {
        log.info("Request for report of last 24hrs");

        OffsetDateTime start = OffsetDateTime.now(ZoneId.of("UTC")).minusDays(1);
        OffsetDateTime end = OffsetDateTime.now(ZoneId.of("UTC"));
        TimeRange timeRange = new TimeRange(start, end);

        Report report = pageVisitReportService.createReportForTimeRange(timeRange);

        return ReportDto.fromDomain(report);
    }
}
