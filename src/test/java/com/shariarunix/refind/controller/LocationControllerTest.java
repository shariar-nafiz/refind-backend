package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.location.DistrictResponse;
import com.shariarunix.refind.dto.location.LocationResponse;
import com.shariarunix.refind.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
    }

    @Test
    @DisplayName("GET /api/v1/locations/divisions returns list of divisions")
    void getAllDivisions_Success() throws Exception {
        when(locationService.getAllDivisions()).thenReturn(List.of("Barishal", "Chattogram", "Dhaka"));

        mockMvc.perform(get("/api/v1/locations/divisions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[2]").value("Dhaka"));
    }

    @Test
    @DisplayName("GET /api/v1/locations/districts returns district summaries")
    void getAllDistricts_Success() throws Exception {
        DistrictResponse district = DistrictResponse.builder()
                .division("Dhaka")
                .district("Dhaka")
                .thanaCount(50L)
                .build();
        when(locationService.getAllDistricts()).thenReturn(List.of(district));

        mockMvc.perform(get("/api/v1/locations/districts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].district").value("Dhaka"))
                .andExpect(jsonPath("$.data[0].thanaCount").value(50));
    }

    @Test
    @DisplayName("GET /api/v1/locations/thanas?district=Dhaka returns thanas in district")
    void getThanasByDistrict_Success() throws Exception {
        LocationResponse thana = LocationResponse.builder()
                .id(10L)
                .division("Dhaka")
                .district("Dhaka")
                .thana("Dhanmondi")
                .isActive(true)
                .build();
        when(locationService.getThanasByDistrict("Dhaka")).thenReturn(List.of(thana));

        mockMvc.perform(get("/api/v1/locations/thanas")
                        .param("district", "Dhaka")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].thana").value("Dhanmondi"));
    }
}
