package com.karl.pixeltracker.domain.report;

import java.time.OffsetDateTime;

public record TimeRange(OffsetDateTime start, OffsetDateTime end) {
}
