package com.marketplace.second_hand_clothes.service;

import com.marketplace.second_hand_clothes.rest.dto.GarmentInfoCriteria;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GarmentService {

    List<GarmentDto> searchGarments(GarmentInfoCriteria garmentInfoCriteria, Pageable pageable);
    GarmentDto publishGarment(GarmentDto garmentDto) throws Exception;
    GarmentDto updateGarment(Long id, GarmentDto garmentDto) throws Exception;
    void unpublishGarment(Long id) throws Exception;
}
