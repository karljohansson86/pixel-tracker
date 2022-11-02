package com.karl.pixeltracker.adapter.rest;


import com.karl.pixeltracker.domain.user.User;

import javax.servlet.http.Cookie;
import java.util.UUID;

public class CookieFactory {

    private static final String USER_ID_COOKIE_NAME = "user_id";
    private static final int ONE_DAY_IN_SECONDS = 86400;

    private CookieFactory() {
        //Not instantiable
    }

    public static Cookie create(User user) {
        UUID userId = user.userId();
        Cookie cookie = new Cookie(USER_ID_COOKIE_NAME, userId.toString());
        cookie.setMaxAge(ONE_DAY_IN_SECONDS);

        return cookie;
    }
}
