package com.pc.greenbay.service;

import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.model.response.BidCommonResponseDTO;
import com.pc.greenbay.model.response.BidListDTO;
import com.pc.greenbay.entity.User;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BidService {

    Bid saveBid(Bid bid);

    BidCommonResponseDTO placeBid(UUID itemId, User bidder, int bidAmount);

    List<BidListDTO> findBidsByItem(Item item);

    Map<String, String> buildErrorResponseForBiding(BindingResult bindingResult);
}
