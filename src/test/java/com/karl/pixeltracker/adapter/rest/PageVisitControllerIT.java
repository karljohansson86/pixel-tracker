package com.karl.pixeltracker.adapter.rest;

import com.karl.pixeltracker.TestPostgresContainer;
import com.karl.pixeltracker.adapter.repository.TestDbUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PageVisitControllerIT {

    private static final String USER_ID_COOKIE_NAME = "user_id";
    private static final String REFERER_HEADER = "referer";
    private static final String GET_IMAGE_URI = "/v1/image";

    private static final PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private TestDbUtils testDbUtils;

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        testDbUtils = new TestDbUtils(jdbcTemplate);
    }

    @Test
    void should_get_image_and_cookie_with_user_id_when_first_time_visitor() {
        String expectedUrl = "/contact.html";

        WebTestClient.ResponseSpec actual = webTestClient
                .get()
                .uri(GET_IMAGE_URI)
                .header(REFERER_HEADER, expectedUrl)
                .exchange();

        actual
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.IMAGE_GIF)
                .expectCookie().exists(USER_ID_COOKIE_NAME);

    }

    @Test
    void should_get_image_and_cookie_with_same_user_id_when_recurring_visitor() {
        String expectedUrl = "/contact.html";
        UUID expectedUserId = UUID.fromString("8fadbfc0-dd70-46a5-9b98-b11cf5a317c7");

        testDbUtils.insertUser(expectedUserId);

        WebTestClient.ResponseSpec actual = webTestClient
                .get()
                .uri(GET_IMAGE_URI)
                .header(REFERER_HEADER, expectedUrl)
                .cookie(USER_ID_COOKIE_NAME, expectedUserId.toString())
                .exchange();

        actual
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.IMAGE_GIF)
                .expectCookie().exists(USER_ID_COOKIE_NAME)
                .expectCookie().valueEquals(USER_ID_COOKIE_NAME, expectedUserId.toString());
    }

    @Test
    void should_get_bad_request_when_referer_header_missing() {
        WebTestClient.ResponseSpec actual = webTestClient
                .get()
                .uri(GET_IMAGE_URI)
                .exchange();

        actual
                .expectStatus().isBadRequest();

    }
}