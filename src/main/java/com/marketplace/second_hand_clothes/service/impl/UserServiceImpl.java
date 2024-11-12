package com.marketplace.second_hand_clothes.service.impl;

import com.marketplace.second_hand_clothes.model.User;
import com.marketplace.second_hand_clothes.repository.UserRepository;
import com.marketplace.second_hand_clothes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
    }
}
