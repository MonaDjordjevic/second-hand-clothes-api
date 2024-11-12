package com.marketplace.second_hand_clothes.repository;

import com.marketplace.second_hand_clothes.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        var user = User.builder()
                .username("johndoe")
                .password("password")
                .address("address")
                .fullName("John Doe")
                .build();
        userRepository.save(user);
    }

    @Test
    void findByUsername() {
        var result = userRepository.findByUsername("johndoe");
        assertThat(result).isNotNull();
        assertThat(result.get().getUsername()).isEqualTo("johndoe");
        assertThat(result.get().getPassword()).isEqualTo("password");
    }
}