package com.shariarunix.refind.repository;

import com.shariarunix.refind.entity.Item;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Page<Item> findByUserId(Long userId, Pageable pageable);

    Page<Item> findByUserIdAndType(Long userId, ItemType type, Pageable pageable);

    Page<Item> findByUserIdAndStatus(Long userId, ItemStatus status, Pageable pageable);

    Optional<Item> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Item i SET i.viewCount = i.viewCount + 1 WHERE i.id = :id")
    void incrementViewCount(@Param("id") Long id);

    long countByUserId(Long userId);

    long countByStatus(ItemStatus status);

    long countByType(ItemType type);

    List<Item> findTop10ByTypeAndStatusOrderByCreatedAtDesc(ItemType type, ItemStatus status);
}
