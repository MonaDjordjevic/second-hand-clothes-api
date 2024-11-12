package com.marketplace.second_hand_clothes.converter.impl;

import com.marketplace.second_hand_clothes.converter.DtoEntityConverter;
import com.marketplace.second_hand_clothes.model.Garment;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GarmentConverter implements DtoEntityConverter<GarmentDto, Garment> {

    private final UserConverter userConverter;

    @Override
    public GarmentDto toDto(Garment garment) {
        return GarmentDto.builder()
                .id(garment.getId())
                .description(garment.getDescription())
                .price(garment.getPrice())
                .publisher(userConverter.toDto(garment.getPublisher()))
                .size(garment.getSize())
                .type(garment.getType())
                .build();
    }

    @Override
    public Garment toEntity(GarmentDto garmentDto) {
        return Garment.builder()
                .description(garmentDto.getDescription())
                .type(garmentDto.getType())
                .price(garmentDto.getPrice())
                .size(garmentDto.getSize())
                .build();
    }
}
