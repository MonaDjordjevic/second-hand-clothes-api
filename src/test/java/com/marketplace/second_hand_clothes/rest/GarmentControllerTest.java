package com.marketplace.second_hand_clothes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.second_hand_clothes.config.SecurityConfig;
import com.marketplace.second_hand_clothes.rest.dto.GarmentInfoCriteria;
import com.marketplace.second_hand_clothes.rest.dto.GarmentDto;
import com.marketplace.second_hand_clothes.rest.dto.UserDto;
import com.marketplace.second_hand_clothes.security.JwtUtil;
import com.marketplace.second_hand_clothes.service.GarmentService;
import com.marketplace.second_hand_clothes.service.impl.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GarmentController.class)
@Import(SecurityConfig.class)
class GarmentControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pageable FIRST_PAGE = PageRequest.of(0, 1);
    private static final GarmentDto TEST_GARMENT_DTO = createFirstTestGarmentDto();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GarmentService garmentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @Test
    void searchGarmentsReturnsGarmentsByInfoCriteria() throws Exception {
        doReturn(List.of(TEST_GARMENT_DTO))
                .when(garmentService).searchGarments(any(GarmentInfoCriteria.class), any(Pageable.class));

        mvc.perform(get("/clothes")
                        .param("page", "0")
                        .param("type", "Skirt")
                        .param("size", "M"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].size").value(TEST_GARMENT_DTO.getSize()))
                .andExpect(jsonPath("$[0].type").value(TEST_GARMENT_DTO.getType()));
    }

    @Test
    @WithMockUser
    void publishGarmentPublishesGarment() throws Exception {
        doReturn(TEST_GARMENT_DTO)
                .when(garmentService).publishGarment(any(GarmentDto.class));

        mvc.perform(post("/clothes/publish")
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(TEST_GARMENT_DTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size").value(TEST_GARMENT_DTO.getSize()))
                .andExpect(jsonPath("$.type").value(TEST_GARMENT_DTO.getType()));
    }

    @Test
    void publishGarmentReturnsUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mvc.perform(put("/clothes/update/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(TEST_GARMENT_DTO)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(garmentService);
    }

    @Test
    @WithMockUser
    void updateGarmentUpdatesGarment() throws Exception {
        doReturn(TEST_GARMENT_DTO)
                .when(garmentService).updateGarment(anyLong(), any(GarmentDto.class));

        mvc.perform(put("/clothes/update/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(TEST_GARMENT_DTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size").value(TEST_GARMENT_DTO.getSize()))
                .andExpect(jsonPath("$.type").value(TEST_GARMENT_DTO.getType()));
    }

    @Test
    void updateGarmentReturnsUnauthorizedWhenIsNotAuthenticated() throws Exception {
        mvc.perform(put("/clothes/update/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(TEST_GARMENT_DTO)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(garmentService);
    }

    @Test
    @WithMockUser
    public void testUnpublishGarment() throws Exception {
        doNothing().when(garmentService).unpublishGarment(anyLong());

        mvc.perform(delete("/clothes/unpublish/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"));
    }

    @Test
    void unpublishGarmentReturnsUnauthorizedWhenIsNotAuthenticated() throws Exception {
        mvc.perform(delete("/clothes/unpublish/{id}", 1L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(garmentService);
    }

    private static GarmentDto createFirstTestGarmentDto() {
        return GarmentDto.builder()
                .id(1L)
                .type("Skirt")
                .size("M")
                .price(BigDecimal.valueOf(22.4))
                .description("Pink shirt")
                .publisher(createTestUserDto())
                .build();
    }

    private static UserDto createTestUserDto() {
        return UserDto
                .builder()
                .id(1L)
                .address("Street")
                .fullName("Mona")
                .build();
    }
}