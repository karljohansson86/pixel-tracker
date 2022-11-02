package com.karl.pixeltracker.ports;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;

public interface PageVisitTracker {

    User handle(PageVisit pageVisit);

}
