package com.karl.pixeltracker;

import com.karl.pixeltracker.domain.pagevisit.PageVisit;
import com.karl.pixeltracker.domain.user.User;
import com.karl.pixeltracker.ports.repository.PageVisitRepository;
import com.karl.pixeltracker.ports.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PageVisitTrackerServiceTest {

    private static final String EXPECTED_URL = "/contact.html";
    private static final UUID EXPECTED_USER_ID = UUID.fromString("c2fe8b94-b3c9-4ee3-ba1f-737917de49fe");
    private static final UUID NON_EXISTING_USER_ID = UUID.fromString("45b3062e-ab15-4754-864c-98bebc887e56");

    private PageVisitTrackerService service;
    private UserRepository userRepository;
    private PageVisitRepository pageVisitRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        pageVisitRepository = mock(PageVisitRepository.class);
        service = new PageVisitTrackerService(userRepository, pageVisitRepository);
    }

    @Test
    void should_register_visitor_and_generate_new_userid_when_first_time_visitor() {
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(null, EXPECTED_URL, expectedTimestamp);

        User actual = service.handle(pageVisit);

        assertThat(actual).isNotNull();
        verify(userRepository, times(0)).find(any());
        verify(userRepository).store(any(User.class));
        verify(pageVisitRepository).register(any(User.class), eq(pageVisit));
    }

    @Test
    void should_register_visitor_when_recurring_visitor_with_userid() {
        User expectedUser = new User(EXPECTED_USER_ID);
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(EXPECTED_USER_ID, EXPECTED_URL, expectedTimestamp);

        when(userRepository.find(EXPECTED_USER_ID)).thenReturn(Optional.of(expectedUser));

        User actual = service.handle(pageVisit);

        assertThat(actual).isNotNull();
        assertThat(actual.userId()).isEqualTo(EXPECTED_USER_ID);
        verify(userRepository).find(EXPECTED_USER_ID);
        verify(userRepository, times(0)).store(any(User.class));
        verify(pageVisitRepository).register(expectedUser, pageVisit);
    }

    @Test
    void should_register_visitor_and_generate_new_userid_when_recurring_visitor_with_unknown_userid() {
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(ZoneId.of("UTC"));
        PageVisit pageVisit = new PageVisit(NON_EXISTING_USER_ID, EXPECTED_URL, expectedTimestamp);
        when(userRepository.find(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        User actual = service.handle(pageVisit);

        assertThat(actual).isNotNull();
        assertThat(actual.userId()).isNotEqualTo(NON_EXISTING_USER_ID);
        verify(userRepository).find(NON_EXISTING_USER_ID);
        verify(userRepository).store(any(User.class));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(pageVisitRepository).register(userArgumentCaptor.capture(), eq(pageVisit));
        User actualUser = userArgumentCaptor.getValue();
        assertThat(actualUser.userId()).isNotEqualTo(NON_EXISTING_USER_ID);
    }
}