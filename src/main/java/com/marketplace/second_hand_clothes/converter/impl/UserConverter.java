package com.marketplace.second_hand_clothes.converter.impl;

import com.marketplace.second_hand_clothes.converter.DtoEntityConverter;
import com.marketplace.second_hand_clothes.model.User;
import com.marketplace.second_hand_clothes.rest.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserConverter implements DtoEntityConverter<UserDto, User> {

    @Override
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .address(userDto.getAddress())
                .fullName(userDto.getFullName())
                .build();
    }
}
