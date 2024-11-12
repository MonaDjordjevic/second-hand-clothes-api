package com.marketplace.second_hand_clothes.service.impl;

import com.marketplace.second_hand_clothes.converter.impl.GarmentConverter;
import com.marketplace.second_hand_clothes.model.Garment;
import com.marketplace.second_hand_clothes.rest.dto.GarmentInfoCriteria;
import com.marketplace.second_hand_clothes.model.User;
import com.marketplace.second_hand_clothes.repository.GarmentRepository;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import com.marketplace.second_hand_clothes.rest.dto.UserDto;
import com.marketplace.second_hand_clothes.service.UserService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@AllArgsConstructor
class GarmentServiceImplTest {

    private static final Pageable FIRST_PAGE = PageRequest.of(0, 1);
    private static final ExampleMatcher MATCHER = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

    private final GarmentRepository garmentRepository = mock(GarmentRepository.class);
    private final GarmentConverter garmentConverter = mock(GarmentConverter.class);
    private final SecurityContext securityContext = mock(SecurityContext.class);
    private final Authentication authentication = mock(Authentication.class);
    private final UserService userService = mock(UserService.class);
    private final GarmentServiceImpl garmentService = new GarmentServiceImpl(garmentRepository, garmentConverter, userService);


    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("mona");
        doReturn(createFirstTestGarmentDto(createTestUserDto()))
                .when(garmentConverter).toDto(createFirstTestGarment());
        doReturn(createFirstTestGarment())
                .when(garmentConverter).toEntity(createFirstTestGarmentDto(null));
        doReturn(Optional.of(createFirstTestGarment()))
                .when(garmentRepository).findById(1L);
    }

    @Test
    void searchGarmentsReturnsGarmentBySearchCriteria() {
        var garmentInfoCriteria = createTestGarmentInfoCriteria();
        var example = Example.of(Garment
                .builder()
                .type(garmentInfoCriteria.getType())
                .size(garmentInfoCriteria.getSize())
                .price(garmentInfoCriteria.getPrice())
                .build(), MATCHER);
        var testGarment = createFirstTestGarment();

        doReturn(new PageImpl<>(List.of(testGarment)))
                .when(garmentRepository).findAll(example, FIRST_PAGE);
        var result = garmentService.searchGarments(garmentInfoCriteria, FIRST_PAGE);
        assertThat(result).isEqualTo(List.of(createFirstTestGarmentDto(createTestUserDto())));
    }

    @Test
    void searchGarmentsReturnsAllGarmentsWhenNoCriteriaProvided() {
        var garmentInfoCriteria = GarmentInfoCriteria
                .builder()
                .type(null)
                .size(null)
                .price(null)
                .build();
        var example = Example.of(new Garment(), MATCHER);
        var garmentList = List.of(createFirstTestGarment(), createSecondTestGarment());
        doReturn(new PageImpl<>(garmentList))
                .when(garmentRepository).findAll(example, FIRST_PAGE);
        doReturn(createSecondTestGarmentDto())
                .when(garmentConverter).toDto(createSecondTestGarment());
        var result = garmentService.searchGarments(garmentInfoCriteria, FIRST_PAGE);
        assertThat(result).isEqualTo(List.of(createFirstTestGarmentDto(createTestUserDto()), createSecondTestGarmentDto()));
    }

    @Test
    void publishGarmentPublishesGarmentSuccessfully() {
        doReturn(createTestUser())
                .when(userService).findByUsername("mona");
        doReturn(createFirstTestGarment())
                .when(garmentRepository).save(createFirstTestGarment());

        var garmentToPublish = createFirstTestGarmentDto(null);
        var result = garmentService.publishGarment(garmentToPublish);
        var expectedGarment = createFirstTestGarmentDto(createTestUserDto());
        assertThat(result).isEqualTo(expectedGarment);
    }

    @Test
    void publishGarmentThrowsExceptionWhenUserDoesNotExist() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"))
                .when(userService).findByUsername("mona");
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> garmentService.publishGarment(createFirstTestGarmentDto(null)))
                .withMessageContaining("User does not exist");
    }

    @Test
    void updateGarmentUpdatesGarmentByHisPublisherSuccessfully() throws Exception {
        var updatedGarment = createFirstTestGarment();
        updatedGarment.setType("Jeans");
        updatedGarment.setDescription("Blue jeans");

        var updatedGarmentDto = createFirstTestGarmentDto(createTestUserDto());
        updatedGarmentDto.setType("Jeans");
        updatedGarmentDto.setDescription("Blue jeans");

        doReturn(updatedGarment)
                .when(garmentRepository).save(updatedGarment);
        doReturn(updatedGarmentDto)
                .when(garmentConverter).toDto(updatedGarment);

        var garmentDataToUpdate = GarmentDto.builder().type("Jeans").description("Blue jeans").build();
        var result = garmentService.updateGarment(1L, garmentDataToUpdate);
        assertThat(result).isEqualTo(updatedGarmentDto);
    }

    @Test
    void unpublishGarmentUnpublishGarmentSuccessfully() throws Exception {
        garmentService.unpublishGarment(1L);
        verify(garmentRepository).delete(createFirstTestGarment());
    }

    @Test
    void updateGarmentThrowsExceptionWhenUserIsNotPublisherOfThatGarment() throws Exception {
        when(authentication.getName()).thenReturn("otherUser");

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> garmentService.updateGarment(1L, createFirstTestGarmentDto(null)))
                .withMessageContaining("Garment does not belong to user");
    }

    @Test
    void unpublishGarmentThrowsExceptionWhenUserIsNotPublisherOfThatGarment() throws Exception {
        when(authentication.getName()).thenReturn("otherUser");

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> garmentService.unpublishGarment(1L))
                .withMessageContaining("Garment does not belong to user");
    }

    @Test
    void updateGarmentThrowsExceptionWhenGarmentWithThatIdDoesNotExist() throws Exception {
        doReturn(Optional.empty())
                .when(garmentRepository).findById(1L);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> garmentService.updateGarment(1L, createFirstTestGarmentDto(null)))
                .withMessageContaining("Garment does not exist");
    }

    @Test
    void unpublishGarmentThrowsExceptionWhenGarmentWithThatIdDoesNotExist() throws Exception {
        doReturn(Optional.empty())
                .when(garmentRepository).findById(1L);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> garmentService.unpublishGarment(1L))
                .withMessageContaining("Garment does not exist");
    }

    private GarmentDto createFirstTestGarmentDto(UserDto user) {
        return GarmentDto.builder()
                .id(1L)
                .type("Skirt")
                .size("M")
                .price(BigDecimal.valueOf(22.4))
                .description("Pink shirt")
                .publisher(user)
                .build();
    }

    private GarmentDto createSecondTestGarmentDto() {
        return GarmentDto.builder()
                .id(1L)
                .type("Jacket")
                .size("L")
                .price(BigDecimal.valueOf(55.4))
                .description("Black jacket")
                .publisher(createTestUserDto())
                .build();
    }

    private GarmentInfoCriteria createTestGarmentInfoCriteria() {
        return GarmentInfoCriteria
                .builder()
                .price(BigDecimal.valueOf(22.4))
                .size("M")
                .type("Skirt")
                .build();
    }

    private Garment createFirstTestGarment() {
        return Garment
                .builder()
                .id(1L)
                .type("Skirt")
                .size("M")
                .price(BigDecimal.valueOf(22.4))
                .description("Pink shirt")
                .publisher(createTestUser())
                .build();
    }

    private Garment createSecondTestGarment() {
        return Garment
                .builder()
                .id(2L)
                .type("Jacket")
                .size("L")
                .price(BigDecimal.valueOf(55.4))
                .description("Black jacket")
                .publisher(createTestUser())
                .build();
    }


    private User createTestUser() {
        return User
                .builder()
                .id(1L)
                .password("pass")
                .address("Street")
                .username("mona")
                .fullName("Mona")
                .build();
    }

    private UserDto createTestUserDto() {
        return UserDto
                .builder()
                .id(1L)
                .address("Street")
                .fullName("Mona")
                .build();
    }
}