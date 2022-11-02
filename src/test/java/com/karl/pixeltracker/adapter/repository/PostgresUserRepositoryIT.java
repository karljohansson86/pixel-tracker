package com.karl.pixeltracker.adapter.repository;

import com.karl.pixeltracker.TestPostgresContainer;
import com.karl.pixeltracker.domain.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostgresUserRepositoryIT {

    private static PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();
    private PostgresUserRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private TestDbUtils testDbUtils;

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        repository = new PostgresUserRepository(jdbcTemplate);
        testDbUtils = new TestDbUtils(jdbcTemplate);
    }

    @Test
    void should_store_user() {
        UUID expectedUserId = UUID.fromString("f59f20dc-c8fd-452e-ae36-0d3b9a02ddcb");
        User user = new User(expectedUserId);

        Integer usersInDbBeforeInsert = testDbUtils.getNumberOfUsersInDb(expectedUserId);
        assertThat(usersInDbBeforeInsert).isEqualTo(0);

        repository.store(user);

        Integer usersInDbAfterInsert = testDbUtils.getNumberOfUsersInDb(expectedUserId);
        assertThat(usersInDbAfterInsert).isEqualTo(1);
    }

    @Test
    void should_do_nothing_if_user_already_present() {
        UUID expectedUserId = UUID.fromString("196a224a-3d16-474c-aba2-34acc78e4c59");
        User user = new User(expectedUserId);

        testDbUtils.insertUser(expectedUserId);
        Integer usersInDbBeforeInsert = testDbUtils.getNumberOfUsersInDb(expectedUserId);
        assertThat(usersInDbBeforeInsert).isEqualTo(1);

        repository.store(user);

        Integer usersInDbAfterInsert = testDbUtils.getNumberOfUsersInDb(expectedUserId);
        assertThat(usersInDbAfterInsert).isEqualTo(1);
    }

    @Test
    void should_find_user() {
        UUID expectedUserId = UUID.fromString("8e295c48-f98c-46a2-bc9a-ca5083458ca4");

        testDbUtils.insertUser(expectedUserId);

        Optional<User> maybeUser = repository.find(expectedUserId);

        assertThat(maybeUser).isPresent();
        User user = maybeUser.get();
        assertThat(user.userId()).isEqualTo(expectedUserId);
    }

    @Test
    void should_not_find_user_when_user_not_present_in_db() {
        UUID expectedUserId = UUID.fromString("4b00bde1-2d96-4ccd-b001-c31947ad0385");

        Optional<User> maybeUser = repository.find(expectedUserId);

        assertThat(maybeUser).isEmpty();
    }

}
