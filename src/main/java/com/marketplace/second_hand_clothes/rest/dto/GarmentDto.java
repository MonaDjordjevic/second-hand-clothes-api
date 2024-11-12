package com.marketplace.second_hand_clothes.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GarmentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String type;
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UserDto publisher;
    private String size;
    private BigDecimal price;
}
