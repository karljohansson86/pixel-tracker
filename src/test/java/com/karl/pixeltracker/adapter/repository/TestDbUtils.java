package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestDbUtils {

    private static final List<String> TABLE_NAMES = List.of("visits", "users");
    private static final TestPageVisitsRowMapper pageVisitsRowMapper = new TestPageVisitsRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TestDbUtils(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertUser(UUID userId) {
        String sql = "INSERT INTO users (user_id) VALUES (:user_id)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);

        jdbcTemplate.update(sql, params);
    }

    public void insertMultiplePageVisits(PageVisit pageVisit, int noOfTimes) {
        for (int i = 0; i < noOfTimes; i++) {
            insertPageVisit(pageVisit);
        }
    }

    public void insertPageVisit(PageVisit pageVisit) {
        UUID userId = pageVisit.userId();
        String url = pageVisit.url();
        OffsetDateTime timestamp = pageVisit.timestamp();

        String sql = "INSERT INTO visits (user_id, url, visited_at) VALUES (:user_id, :url, :visited_at)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("url", url)
                .addValue("visited_at", timestamp);

        jdbcTemplate.update(sql, params);
    }

    public Integer getNumberOfUsersInDb(UUID userId) {
        String sql = "select count(*) from users where user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public List<PageVisit> getPageVisitsForUserId(UUID userId) {
        String sql = "select user_id, url, visited_at from visits where user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);

        return jdbcTemplate.query(sql, params, pageVisitsRowMapper);
    }

    public void clearTables() {
        TABLE_NAMES.forEach(tableName -> {
            String sql = "DELETE FROM " + tableName + ";";
            jdbcTemplate.update(sql, Map.of());
        });
    }
}
