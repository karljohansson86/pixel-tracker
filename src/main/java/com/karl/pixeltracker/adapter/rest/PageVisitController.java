package com.karl.pixeltracker.adapter.rest;


import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;
import com.karl.pixeltracker.ports.PageVisitTracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@ResponseBody
@RequestMapping("/v1")
public class PageVisitController {

    private final PageVisitTracker pageVisitTracker;

    public PageVisitController(PageVisitTracker pageVisitTracker) {
        this.pageVisitTracker = pageVisitTracker;
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] getImage(@CookieValue(value = "user_id", required = false) UUID userId,
                           @RequestHeader(value = "referer") String url,
                           HttpServletResponse response) throws IOException {
        log.info("Received request from user_id {} and url {}", userId, url);

        OffsetDateTime timestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(userId, url, timestamp);

        User user = pageVisitTracker.handle(pageVisit);

        Cookie userIdCookie = CookieFactory.create(user);
        response.addCookie(userIdCookie);

        return getTransparentGifAsByteArray();
    }

    private byte[] getTransparentGifAsByteArray() throws IOException {
        InputStream in = getClass().getResourceAsStream("/gif/transparent_tracking.gif");
        return IOUtils.toByteArray(Objects.requireNonNull(in));
    }
}
