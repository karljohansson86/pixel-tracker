package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.user.User;
import com.karl.pixeltracker.ports.repository.UserRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresUserRepository implements UserRepository {

    private static UserRowMapper userRowMapper = new UserRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> find(UUID userId) {
        String sql = "select user_id from users where user_id = :user_id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);

        List<User> result = jdbcTemplate.query(sql, params, userRowMapper);

        if (result.size() == 1) {
            User user = result.get(0);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void store(User user) {
        String sql = "INSERT INTO users (user_id) VALUES (:user_id) ON CONFLICT DO NOTHING";

        UUID userId = user.userId();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);

        jdbcTemplate.update(sql, params);
    }
}
