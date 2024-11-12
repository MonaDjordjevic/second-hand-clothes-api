package com.marketplace.second_hand_clothes.service;

import com.marketplace.second_hand_clothes.model.User;

public interface UserService {

    User findByUsername(String username);
}
