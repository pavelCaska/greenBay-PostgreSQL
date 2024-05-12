package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID>, PagingAndSortingRepository<Item, UUID> {
    Optional<Item> findItemById(UUID id);
    Page<Item> findAllBySellableTrue(Pageable pageable);

}
