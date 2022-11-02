package com.karl.pixeltracker;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.Report;
import com.karl.pixeltracker.domain.report.TimeRange;
import com.karl.pixeltracker.ports.PageVisitReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@SpringBootApplication
public class PixelTrackerApplication implements CommandLineRunner {

    @Value("${report.generate}")
    private boolean shouldGenerateReport;

    @Autowired
    private PageVisitReporter pageVisitReporter;

    public static void main(String[] args) {
        SpringApplication.run(PixelTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!shouldGenerateReport) {
            return;
        }

        OffsetDateTime start = OffsetDateTime.now(ZoneId.of("UTC")).minusDays(1);
        OffsetDateTime end = OffsetDateTime.now(ZoneId.of("UTC"));
        TimeRange timeRange = new TimeRange(start, end);

        Report reportForTimeRange = pageVisitReporter.createReportForTimeRange(timeRange);

        System.out.println();
        System.out.println("Generating report for last 24hrs");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("|url           \t\t\t\t\t|page views \t|visitors\t|");
        for (PageVisitRecord record : reportForTimeRange.records()) {
            String url = record.url();
            int pageViews = record.pageViews();
            int visitors = record.visitors();
            System.out.println("|" + url + "           \t|" + pageViews + "  \t\t|" + visitors + "\t\t|");
        }
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println();
    }

}
