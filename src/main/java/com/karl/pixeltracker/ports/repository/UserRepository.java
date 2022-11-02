package com.karl.pixeltracker.ports.repository;

import com.karl.pixeltracker.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> find(UUID userId);

    void store(User user);

}
