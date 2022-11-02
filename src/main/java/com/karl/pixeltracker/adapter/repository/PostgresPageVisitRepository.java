package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.report.PageVisitRecord;
import com.karl.pixeltracker.domain.report.TimeRange;
import com.karl.pixeltracker.domain.user.User;
import com.karl.pixeltracker.ports.repository.PageVisitRecordsRepository;
import com.karl.pixeltracker.ports.repository.PageVisitRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class PostgresPageVisitRepository implements PageVisitRepository, PageVisitRecordsRepository {

    private final PageVisitRecordRowMapper pageVisitRecordRowMapper = new PageVisitRecordRowMapper();
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresPageVisitRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void register(User user, PageVisit pageVisit) {
        UUID userId = user.userId();
        String url = pageVisit.url();
        OffsetDateTime timestamp = pageVisit.timestamp();
        String sql = "INSERT INTO visits (user_id, url, visited_at) VALUES (:user_id, :url, :visited_at)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("url", url)
                .addValue("visited_at", timestamp);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<PageVisitRecord> getPageVisitRecordsForTimeRange(TimeRange timeRange) {
        OffsetDateTime start = timeRange.start();
        OffsetDateTime end = timeRange.end();

        String sql = "select url, count(url) as page_views, count(distinct user_id) as visitors from visits where visited_at between :start_time and :end_time group by url;";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start_time", start)
                .addValue("end_time", end);

        return jdbcTemplate.query(sql, params, pageVisitRecordRowMapper);
    }
}
