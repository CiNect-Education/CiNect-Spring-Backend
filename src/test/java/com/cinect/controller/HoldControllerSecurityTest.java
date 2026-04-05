package com.cinect.controller;

import com.cinect.config.CorsConfig;
import com.cinect.repository.UserRepository;
import com.cinect.security.JwtAuthFilter;
import com.cinect.security.JwtService;
import com.cinect.security.SecurityConfig;
import com.cinect.service.AuditLogService;
import com.cinect.service.HoldService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HoldController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthFilter.class})
class HoldControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuditLogService auditLogService;

    @MockBean
    HoldService holdService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserRepository userRepository;

    @Test
    void postHolds_withoutBearer_isRejected() throws Exception {
        var body = Map.of(
                "showtimeId", UUID.randomUUID().toString(),
                "seatIds", List.of(UUID.randomUUID().toString())
        );
        mockMvc.perform(post("/api/v1/holds")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
