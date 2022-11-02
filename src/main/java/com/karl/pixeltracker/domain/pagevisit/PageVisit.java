package com.karl.pixeltracker.domain.pagevisit;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PageVisit(UUID userId, String url, OffsetDateTime timestamp) {
}
