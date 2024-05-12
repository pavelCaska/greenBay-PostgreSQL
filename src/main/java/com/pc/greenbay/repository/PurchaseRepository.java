package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByItem(Item item);
}
