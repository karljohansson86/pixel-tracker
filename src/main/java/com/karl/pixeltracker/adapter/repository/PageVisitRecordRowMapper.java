package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.report.PageVisitRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PageVisitRecordRowMapper implements RowMapper<PageVisitRecord> {
    @Override
    public PageVisitRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        String url = rs.getString("url");
        int pageViews = rs.getInt("page_views");
        int visitors = rs.getInt("visitors");

        return new PageVisitRecord(url, pageViews, visitors);
    }
}
