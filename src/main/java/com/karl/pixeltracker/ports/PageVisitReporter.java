package com.karl.pixeltracker.ports;

import com.karl.pixeltracker.domain.report.Report;
import com.karl.pixeltracker.domain.report.TimeRange;

public interface PageVisitReporter {

    Report createReportForTimeRange(TimeRange timeRange);
}
