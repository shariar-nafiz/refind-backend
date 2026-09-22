package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.location.DistrictResponse;
import com.shariarunix.refind.dto.location.LocationResponse;

import java.util.List;

public interface LocationService {

    List<String> getAllDivisions();

    List<DistrictResponse> getAllDistricts();

    List<String> getDistrictsByDivision(String division);

    List<LocationResponse> getThanasByDistrict(String district);

    LocationResponse getLocationById(Long id);

    List<LocationResponse> searchLocations(String query);
}
