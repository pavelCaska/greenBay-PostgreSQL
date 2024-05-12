package com.pc.greenbay.service;

import com.pc.greenbay.exception.*;
import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.model.response.BidCommonResponseDTO;
import com.pc.greenbay.model.response.BidListDTO;
import com.pc.greenbay.model.response.BidPlacedResponseDTO;
import com.pc.greenbay.model.response.ItemBoughtResponseDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.BidRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final ItemService itemService;
    private final PurchaseService purchaseService;
    private final UserService userService;

    @Autowired
    public BidServiceImpl(BidRepository bidRepository, @Lazy ItemService itemService, PurchaseService purchaseService, UserService userService) {
        this.bidRepository = bidRepository;
        this.itemService = itemService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

//    Only for the purpose of testing
    @Override
    public Bid saveBid(Bid bid) {
        return bidRepository.save(bid);
    }

    @Override
    public Map<String, String> buildErrorResponseForBiding(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
    }

    @Override
    @Transactional
    public BidCommonResponseDTO placeBid(UUID itemId, User bidder, int bidAmount) {
        if(bidder.getBalance() <= 0) {
            throw new NoMoneyException("You have no greenBay dollars, you can't bid.");
        }
        if(bidder.getBalance() < bidAmount) {
            throw new NotEnoughMoneyException("You have not enough greenBay dollars on your account.");
        }

        Item item = itemService.getItemById(itemId);
        if(bidder.getId().equals(item.getSeller().getId())) {
            throw new BidOnOwnItemException("You cannot bid on your own item.");
        }
        if(!item.isSellable()) {
            throw new ItemNotSellableException("Item is not sellable.");
        }
        if(bidAmount < item.getStartingPrice() || bidAmount <= item.getLastBid()) {
            throw new LowBidException("Your bid is too low.");
        }
        if(bidAmount > item.getLastBid() && bidAmount < item.getPurchasePrice() && bidder.getBalance() >= bidAmount) {
            try {
                itemService.saveLastBid(item, bidAmount);
                bidRepository.save(new Bid(item, bidder, bidAmount));
                return new BidPlacedResponseDTO(item.getName(), item.getDescription(), item.getPhotoURL(), item.getSeller().getUsername(), bidAmount);
            } catch (DataAccessException e) {
                throw new DataAccessException("Database error occurred while placing your bid. The operation has failed.") {
                };
            }
        }
        if(bidAmount >= item.getPurchasePrice() && bidder.getBalance() >= bidAmount) {
            try {
                itemService.saveLastBid(item, bidAmount);
                itemService.makeNotSellable(item);
                bidRepository.save(new Bid(item, bidder, bidAmount));
                purchaseService.savePurchase(new Purchase(item, bidder, bidAmount));
                bidder.setBalance(bidder.getBalance() - bidAmount);
                userService.saveUser(bidder);
                return new ItemBoughtResponseDTO(item.getName(), item.getDescription(), item.getPhotoURL(), item.getSeller().getUsername(), bidder.getUsername(), bidAmount);
            } catch (DataAccessException e) {
                throw new DataAccessException("Database error occurred while saving your purchase. The operation has failed.") {
                };
            }
        }
        return null;
    }

    @Override
    public List<BidListDTO> findBidsByItem(Item item) {
        return bidRepository.findAllByItem(item).stream()
                .map(o -> new BidListDTO(o.getId(), o.getBidder().getUsername(), o.getBidAmount())).collect(Collectors.toList());
    }
}
