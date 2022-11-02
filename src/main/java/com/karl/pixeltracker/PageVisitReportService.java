package com.karl.pixeltracker;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.Report;
import com.karl.pixeltracker.domain.report.TimeRange;
import com.karl.pixeltracker.ports.PageVisitReporter;
import com.karl.pixeltracker.ports.repository.PageVisitRecordsRepository;

import java.util.List;

public class PageVisitReportService implements PageVisitReporter {

    private final PageVisitRecordsRepository pageVisitRecordsRepository;

    public PageVisitReportService(PageVisitRecordsRepository pageVisitRecordsRepository) {
        this.pageVisitRecordsRepository = pageVisitRecordsRepository;
    }

    public Report createReportForTimeRange(TimeRange timeRange) {
        List<PageVisitRecord> records = pageVisitRecordsRepository.getPageVisitRecordsForTimeRange(timeRange);
        return new Report(records);
    }

}
