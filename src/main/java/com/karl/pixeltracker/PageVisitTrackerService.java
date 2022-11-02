package com.karl.pixeltracker;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;
import com.karl.pixeltracker.ports.PageVisitTracker;
import com.karl.pixeltracker.ports.repository.PageVisitRepository;
import com.karl.pixeltracker.ports.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class PageVisitTrackerService implements PageVisitTracker {

    private final UserRepository userRepository;
    private final PageVisitRepository pageVisitRepository;

    public PageVisitTrackerService(UserRepository userRepository, PageVisitRepository pageVisitRepository) {
        this.userRepository = userRepository;
        this.pageVisitRepository = pageVisitRepository;
    }

    @Override
    public User handle(PageVisit pageVisit) {
        UUID userId = pageVisit.userId();

        User user = findOrCreateNewUser(userId);
        registerPageVisit(user, pageVisit);

        return user;
    }

    // UserId can be null if it is a first time visitor
    private User findOrCreateNewUser(UUID userId) {
        return Optional.ofNullable(userId)
                .flatMap(userRepository::find)
                .orElseGet(this::createNewUser);
    }

    private User createNewUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId);
        userRepository.store(user);

        return user;
    }

    private void registerPageVisit(User user, PageVisit pageVisit) {
        pageVisitRepository.register(user, pageVisit);
    }
}
