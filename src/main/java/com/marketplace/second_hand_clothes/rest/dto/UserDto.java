package com.marketplace.second_hand_clothes.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long id;
    private String fullName;
    private String address;
}
