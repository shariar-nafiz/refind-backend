package com.shariarunix.refind.repository;

import com.shariarunix.refind.entity.ItemMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMediaRepository extends JpaRepository<ItemMedia, Long> {

    List<ItemMedia> findByItemIdOrderByDisplayOrderAsc(Long itemId);

    void deleteAllByItemId(Long itemId);
}
