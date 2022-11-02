package com.karl.pixeltracker.ports.repository;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;

public interface PageVisitRepository {

    void register(User user, PageVisit pageVisit);
}
