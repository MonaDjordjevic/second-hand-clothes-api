package com.marketplace.second_hand_clothes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.second_hand_clothes.config.SecurityConfig;
import com.marketplace.second_hand_clothes.rest.dto.AuthRequest;
import com.marketplace.second_hand_clothes.security.JwtUtil;
import com.marketplace.second_hand_clothes.service.impl.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @Test
    void authenticateReturnsTokenWhenCredentialsAreValid() throws Exception {
        var authRequest = AuthRequest.builder().username("username").password("password").build();
        var expectedToken = "jwtToken";

        doReturn(expectedToken)
                .when(jwtUtil).generateToken(anyString());

        mvc.perform(post("/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));
    }


    @Test
    void authenticateReturnsUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        var authRequest = AuthRequest.builder().username("invalidUsername").password("invalidPassword").build();

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any());

        mvc.perform(post("/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }
}