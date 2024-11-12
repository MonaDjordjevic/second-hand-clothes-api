package com.marketplace.second_hand_clothes.rest;

import com.marketplace.second_hand_clothes.rest.dto.GarmentInfoCriteria;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import com.marketplace.second_hand_clothes.service.GarmentService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clothes")
public class GarmentController {

    private final GarmentService garmentService;

    @GetMapping
    public List<GarmentDto> searchGarments(@ParameterObject GarmentInfoCriteria garmentInfoCriteria,
                                           @ParameterObject @PageableDefault(size = 100) Pageable pageable) {
        return garmentService.searchGarments(garmentInfoCriteria, pageable);
    }

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public GarmentDto publishGarment(@RequestBody GarmentDto garmentDto) throws Exception {
        return garmentService.publishGarment(garmentDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GarmentDto> updateGarment(@PathVariable Long id, @RequestBody GarmentDto garmentDto) throws Exception {
        return new ResponseEntity<>(garmentService.updateGarment(id, garmentDto), HttpStatus.OK);
    }

    @DeleteMapping("/unpublish/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpublishGarment(@PathVariable Long id) throws Exception {
        garmentService.unpublishGarment(id);
    }
}
