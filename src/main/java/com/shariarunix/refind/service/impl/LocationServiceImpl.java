package com.shariarunix.refind.service.impl;

import com.shariarunix.refind.dto.location.DistrictResponse;
import com.shariarunix.refind.dto.location.LocationResponse;
import com.shariarunix.refind.entity.Location;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.LocationRepository;
import com.shariarunix.refind.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDivisions() {
        return locationRepository.findDistinctDivisions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getAllDistricts() {
        List<Object[]> summaries = locationRepository.findDistrictSummaries();
        return summaries.stream()
                .map(row -> DistrictResponse.builder()
                        .division((String) row[0])
                        .district((String) row[1])
                        .thanaCount((Long) row[2])
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistrictsByDivision(String division) {
        return locationRepository.findDistinctDistrictsByDivision(division);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getThanasByDistrict(String district) {
        return locationRepository.findByDistrictIgnoreCaseAndIsActiveTrueOrderByThanaAsc(district).stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + id));
        return LocationResponse.fromEntity(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> searchLocations(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return locationRepository.searchLocations(query.trim()).stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }
}
