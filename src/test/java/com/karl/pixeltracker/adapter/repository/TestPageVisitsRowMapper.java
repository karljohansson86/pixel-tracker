package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TestPageVisitsRowMapper implements RowMapper<PageVisit> {
    @Override
    public PageVisit mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID user_id = rs.getObject("user_id", UUID.class);
        String url = rs.getString("url");
        OffsetDateTime timestamp = rs.getObject("visited_at", OffsetDateTime.class);

        return new PageVisit(user_id, url, timestamp);
    }

}
