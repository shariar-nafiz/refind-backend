package com.shariarunix.refind.repository;

import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.Role;
import com.shariarunix.refind.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " (u.email IS NOT NULL AND LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           " (u.phone IS NOT NULL AND LOWER(u.phone) LIKE LOWER(CONCAT('%', :query, '%')))) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> searchUsers(
            @Param("query") String query,
            @Param("status") UserStatus status,
            @Param("role") Role role,
            Pageable pageable
    );
}
