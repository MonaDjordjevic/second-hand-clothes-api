package com.marketplace.second_hand_clothes.service.impl;

import com.marketplace.second_hand_clothes.model.User;
import com.marketplace.second_hand_clothes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@AllArgsConstructor
class MyUserDetailsServiceTest {

    private static final String USERNAME = "mona";

    private final UserRepository userRepository = mock(UserRepository.class);
    private final MyUserDetailsService myUserDetailsService = new MyUserDetailsService(userRepository);

    @Test
    void loadUserByUsernameLoadsUser() {
        var userFromDb = createTestUser();
        doReturn(Optional.of(userFromDb))
                .when(userRepository).findByUsername(USERNAME);
        var result = myUserDetailsService.loadUserByUsername(USERNAME);
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getPassword()).isEqualTo(userFromDb.getPassword());
    }

    @Test
    void findByUsernameThrowsExceptionWhenUserWithThatUsernameDoesNotExist() throws Exception {
        doReturn(Optional.empty())
                .when(userRepository).findByUsername(USERNAME);
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> myUserDetailsService.loadUserByUsername(USERNAME))
                .withMessageContaining("User not found");
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