package com.marketplace.second_hand_clothes.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarmentInfoCriteria {

    private String type;
    private String size;
    private BigDecimal price;
}
