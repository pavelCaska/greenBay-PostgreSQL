package com.pc.greenbay.service;

import com.pc.greenbay.exception.RecordNotFoundException;
import com.pc.greenbay.model.ErrorDTO;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.model.request.ItemRequestDTO;
import com.pc.greenbay.model.response.*;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final PurchaseService purchaseService;
    private final BidService bidService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, PurchaseService purchaseService, BidService bidService) {
        this.itemRepository = itemRepository;
        this.purchaseService = purchaseService;
        this.bidService = bidService;
    }

    @Override
    public Map<String, String> buildErrorResponseForItemCreation(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
    }
    @Override
    public ItemResponseDTO createItem(ItemRequestDTO itemRequestDTO, User seller) {
        Item itemToSave = new Item(itemRequestDTO, seller);
            Item savedItem = itemRepository.save(itemToSave);
            return ItemResponseDTO.fromEntity(savedItem);
    }
    @Override
    public boolean isItemSellable(Item item) {
        return item.isSellable();
    }

    @Override
    public List<ItemListDTO> listItems() {
         return itemRepository.findAll().stream()
                 .map(ItemListDTO::new).collect(Collectors.toList());
    }
    @Override
    public Item getItemById(UUID id) {
        return itemRepository.findItemById(id).orElseThrow(() -> new RecordNotFoundException("Item not found."));
    }

    @Override
    public void saveLastBid(Item item, int bidAmount) {
        item.setLastBid(bidAmount);
        itemRepository.save(item);
    }
    @Override
    public void makeNotSellable(Item item) {
        item.setSellable(false);
        itemRepository.save(item);
    }
    @Override
    public ItemCommonResponseDTO showItemDetails(UUID id) {
        Item item = getItemById(id);
        if(!item.isSellable()) {
            Optional<Purchase> optionalPurchase = purchaseService.getPurchaseByItem(item);
            if(optionalPurchase.isEmpty()){
                throw new RecordNotFoundException("Purchase record not found.");
            }
            return new ItemNotSellableResponseDTO(item, optionalPurchase.get()) ;
        }
        List<BidListDTO> bidList = bidService.findBidsByItem(item);
        return new ItemSellableResponseDTO(item, bidList);

    }
    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> getItemsBySellableTrueAndPage(int page) {
        Pageable pageable = PageRequest.of(page, 3); // 3 items per page
        return itemRepository.findAllBySellableTrue(pageable);
    }

    @Override
    public ResponseEntity<?> getItemsPaged(int page) {
        Page<Item> itemPage = getItemsBySellableTrueAndPage(page - 1);

        if (itemPage.hasContent() && page <= itemPage.getTotalPages()) {
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("total_pages", itemPage.getTotalPages());
            response.put("items", itemPage.getContent().stream()
                    .map(ItemPageDTO::new)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO("There is no page: " + page));
        }
    }
}
