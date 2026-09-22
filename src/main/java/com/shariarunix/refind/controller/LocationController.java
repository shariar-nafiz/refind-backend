package com.shariarunix.refind.controller;

import com.shariarunix.refind.dto.common.ApiResponse;
import com.shariarunix.refind.dto.location.DistrictResponse;
import com.shariarunix.refind.dto.location.LocationResponse;
import com.shariarunix.refind.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Public geographic registry of Bangladesh divisions, districts, and thanas")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/divisions")
    @Operation(summary = "Get all divisions", description = "Retrieves a list of all distinct administrative divisions in Bangladesh")
    public ResponseEntity<ApiResponse<List<String>>> getAllDivisions(HttpServletRequest request) {
        List<String> divisions = locationService.getAllDivisions();
        return ResponseEntity.ok(ApiResponse.success("Divisions retrieved successfully", divisions, request.getRequestURI()));
    }

    @GetMapping("/districts")
    @Operation(summary = "Get all districts", description = "Retrieves summary list of all 64 districts with division mapping and thana counts")
    public ResponseEntity<ApiResponse<List<DistrictResponse>>> getAllDistricts(HttpServletRequest request) {
        List<DistrictResponse> districts = locationService.getAllDistricts();
        return ResponseEntity.ok(ApiResponse.success("Districts retrieved successfully", districts, request.getRequestURI()));
    }

    @GetMapping("/districts/by-division")
    @Operation(summary = "Get districts by division", description = "Retrieves all districts belonging to a specific administrative division")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByDivision(
            @RequestParam("division") String division,
            HttpServletRequest request
    ) {
        List<String> districts = locationService.getDistrictsByDivision(division);
        return ResponseEntity.ok(ApiResponse.success("Districts for division retrieved successfully", districts, request.getRequestURI()));
    }

    @GetMapping("/thanas")
    @Operation(summary = "Get thanas by district", description = "Retrieves all thanas/upazilas in a specific district for cascading dropdowns")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getThanasByDistrict(
            @RequestParam("district") String district,
            HttpServletRequest request
    ) {
        List<LocationResponse> thanas = locationService.getThanasByDistrict(district);
        return ResponseEntity.ok(ApiResponse.success("Thanas retrieved successfully", thanas, request.getRequestURI()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search locations", description = "Searches locations matching a district or thana name substring")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> searchLocations(
            @RequestParam("query") String query,
            HttpServletRequest request
    ) {
        List<LocationResponse> locations = locationService.searchLocations(query);
        return ResponseEntity.ok(ApiResponse.success("Locations search completed", locations, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID", description = "Retrieves a single location by its primary key identifier")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        LocationResponse location = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success("Location retrieved successfully", location, request.getRequestURI()));
    }
}
