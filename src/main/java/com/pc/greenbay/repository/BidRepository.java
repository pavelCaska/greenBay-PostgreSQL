package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findAllByItem(Item item);
}
