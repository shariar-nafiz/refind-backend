package com.shariarunix.refind.repository;

import com.shariarunix.refind.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT DISTINCT l.division FROM Location l WHERE l.isActive = true ORDER BY l.division ASC")
    List<String> findDistinctDivisions();

    @Query("SELECT DISTINCT l.district FROM Location l WHERE l.isActive = true ORDER BY l.district ASC")
    List<String> findDistinctDistricts();

    @Query("SELECT DISTINCT l.district FROM Location l WHERE l.division = :division AND l.isActive = true ORDER BY l.district ASC")
    List<String> findDistinctDistrictsByDivision(@Param("division") String division);

    List<Location> findByDistrictIgnoreCaseAndIsActiveTrueOrderByThanaAsc(String district);

    Optional<Location> findByDistrictIgnoreCaseAndThanaIgnoreCase(String district, String thana);

    @Query("SELECT l.division, l.district, COUNT(l.id) FROM Location l WHERE l.isActive = true GROUP BY l.division, l.district ORDER BY l.division ASC, l.district ASC")
    List<Object[]> findDistrictSummaries();

    @Query("SELECT l FROM Location l WHERE l.isActive = true AND (LOWER(l.thana) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.district) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY l.district ASC, l.thana ASC")
    List<Location> searchLocations(@Param("query") String query);
}
