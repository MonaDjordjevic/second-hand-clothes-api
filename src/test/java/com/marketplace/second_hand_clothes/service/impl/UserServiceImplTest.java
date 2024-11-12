package com.marketplace.second_hand_clothes.service.impl;

import com.marketplace.second_hand_clothes.model.User;
import com.marketplace.second_hand_clothes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@AllArgsConstructor
class UserServiceImplTest {

    private static final String USERNAME = "mona";
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    void findByUsernameReturnsUser() {
        doReturn(Optional.of(createTestUser()))
                .when(userRepository).findByUsername(USERNAME);
        var result = userService.findByUsername(USERNAME);
        assertThat(result).isEqualTo(createTestUser());
    }

    @Test
    void findByUsernameThrowsExceptionWhenUserWithThatUsernameDoesNotExist() throws Exception {
        doReturn(Optional.empty())
                .when(userRepository).findByUsername(USERNAME);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> userService.findByUsername(USERNAME))
                .withMessageContaining("User does not exist");
    }

    private User createTestUser() {
        return User
                .builder()
                .id(1L)
                .password("pass")
                .address("Street")
                .username(USERNAME)
                .fullName("Mona")
                .build();
    }
}