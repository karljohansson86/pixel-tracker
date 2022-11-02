package com.karl.pixeltracker.ports.repository;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.TimeRange;

import java.util.List;

public interface PageVisitRecordsRepository {

    List<PageVisitRecord> getPageVisitRecordsForTimeRange(TimeRange timeRange);
}
