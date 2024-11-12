package com.marketplace.second_hand_clothes.service.impl;

import com.marketplace.second_hand_clothes.converter.impl.GarmentConverter;
import com.marketplace.second_hand_clothes.model.Garment;
import com.marketplace.second_hand_clothes.rest.dto.GarmentInfoCriteria;
import com.marketplace.second_hand_clothes.repository.GarmentRepository;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import com.marketplace.second_hand_clothes.service.GarmentService;
import com.marketplace.second_hand_clothes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class GarmentServiceImpl implements GarmentService {

    private final GarmentRepository garmentRepository;
    private final GarmentConverter garmentConverter;
    private final UserService userService;

    @Override
    public List<GarmentDto> searchGarments(GarmentInfoCriteria garmentInfoCriteria, Pageable pageable) {
        var garments = findGarmentsByCriteria(garmentInfoCriteria, pageable);
        var garmentDtos = garments
                .map(garment -> garmentConverter.toDto(garment))
                .toList();
        return garmentDtos;
    }

    @Override
    public GarmentDto publishGarment(GarmentDto garmentDto) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var publisher = userService.findByUsername(username);
        var garment = garmentConverter.toEntity(garmentDto);
        garment.setPublisher(publisher);

        var savedGarment = garmentRepository.save(garment);
        return garmentConverter.toDto(savedGarment);
    }

    @Override
    public GarmentDto updateGarment(Long id, GarmentDto garmentDto) throws Exception {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var garment = garmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Garment does not exist"));
        validateOwnership(garment, username);
        applyNonNullProperties(garmentDto, garment);

        var savedGarment = garmentRepository.save(garment);
        return garmentConverter.toDto(savedGarment);
    }

    @Override
    public void unpublishGarment(Long id) throws Exception {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var garment = garmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Garment does not exist"));
        validateOwnership(garment, username);
        garmentRepository.delete(garment);
    }

    private void validateOwnership(Garment garment, String username) throws Exception {
        if (!garment.getPublisher().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Garment does not belong to user");
        }
    }

    private void applyNonNullProperties(GarmentDto source, Garment target) {
        Optional.ofNullable(source.getType()).ifPresent(target::setType);
        Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
        Optional.ofNullable(source.getSize()).ifPresent(target::setSize);
        Optional.ofNullable(source.getPrice()).ifPresent(target::setPrice);
    }

    private Stream<Garment> findGarmentsByCriteria(GarmentInfoCriteria criteria, Pageable pageable) {
        var probe = new Garment();
        if (StringUtils.hasText(criteria.getType())) {
            probe.setType(criteria.getType());
        }
        if (StringUtils.hasText(criteria.getSize())) {
            probe.setSize(criteria.getSize());
        }
        if (criteria.getPrice() != null) {
            probe.setPrice(criteria.getPrice());
        }

        var matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        var example = Example.of(probe, matcher);
        return garmentRepository.findAll(example, pageable).getContent().stream();
    }
}
