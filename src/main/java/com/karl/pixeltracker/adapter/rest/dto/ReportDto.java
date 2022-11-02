package com.karl.pixeltracker.adapter.rest.dto;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.Report;

import java.util.List;

public record ReportDto(List<PageVisitRecordDto> pageVisits) {

    public static ReportDto fromDomain(Report report) {
        List<PageVisitRecord> records = report.records();
        List<PageVisitRecordDto> recordDtos = records.stream()
                .map(PageVisitRecordDto::fromDomain)
                .toList();

        return new ReportDto(recordDtos);
    }
}
