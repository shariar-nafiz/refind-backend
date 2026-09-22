package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.location.DistrictResponse;
import com.shariarunix.refind.dto.location.LocationResponse;
import com.shariarunix.refind.entity.Location;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.LocationRepository;
import com.shariarunix.refind.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Location dhanmondi;

    @BeforeEach
    void setUp() {
        dhanmondi = Location.builder()
                .division("Dhaka")
                .district("Dhaka")
                .thana("Dhanmondi")
                .isActive(true)
                .build();
        dhanmondi.setId(10L);
    }

    @Test
    @DisplayName("getAllDivisions returns list of divisions")
    void getAllDivisions_Success() {
        when(locationRepository.findDistinctDivisions()).thenReturn(List.of("Barishal", "Chattogram", "Dhaka"));

        List<String> divisions = locationService.getAllDivisions();

        assertThat(divisions).containsExactly("Barishal", "Chattogram", "Dhaka");
    }

    @Test
    @DisplayName("getAllDistricts aggregates summaries")
    void getAllDistricts_Success() {
        Object[] row1 = new Object[]{"Dhaka", "Dhaka", 50L};
        Object[] row2 = new Object[]{"Dhaka", "Gazipur", 6L};
        when(locationRepository.findDistrictSummaries()).thenReturn(List.of(row1, row2));

        List<DistrictResponse> result = locationService.getAllDistricts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDistrict()).isEqualTo("Dhaka");
        assertThat(result.get(0).getThanaCount()).isEqualTo(50L);
    }

    @Test
    @DisplayName("getThanasByDistrict returns thanas in district")
    void getThanasByDistrict_Success() {
        when(locationRepository.findByDistrictIgnoreCaseAndIsActiveTrueOrderByThanaAsc("Dhaka"))
                .thenReturn(List.of(dhanmondi));

        List<LocationResponse> result = locationService.getThanasByDistrict("Dhaka");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getThana()).isEqualTo("Dhanmondi");
    }

    @Test
    @DisplayName("getLocationById throws ResourceNotFoundException when id is missing")
    void getLocationById_NotFound() {
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.getLocationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");
    }
}
