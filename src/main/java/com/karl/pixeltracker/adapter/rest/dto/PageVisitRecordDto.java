package com.karl.pixeltracker.adapter.rest.dto;

import com.karl.pixeltracker.domain.report.PageVisitRecord;

public record PageVisitRecordDto(String url, int pageViews, int visitors) {

    public static PageVisitRecordDto fromDomain(PageVisitRecord pageVisitRecord) {
        String theUrl = pageVisitRecord.url();
        int theNoPageViews = pageVisitRecord.pageViews();
        int theNoVisitors = pageVisitRecord.visitors();

        return new PageVisitRecordDto(theUrl, theNoPageViews, theNoVisitors);
    }
}
