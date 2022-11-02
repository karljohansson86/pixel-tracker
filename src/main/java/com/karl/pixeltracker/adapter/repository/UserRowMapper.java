package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.domain.user.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID userId = rs.getObject("user_id", UUID.class);

        return new User(userId);
    }
}
