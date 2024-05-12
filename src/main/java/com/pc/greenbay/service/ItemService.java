package com.pc.greenbay.service;

import com.pc.greenbay.model.request.ItemRequestDTO;
import com.pc.greenbay.model.response.ItemCommonResponseDTO;
import com.pc.greenbay.model.response.ItemListDTO;
import com.pc.greenbay.model.response.ItemResponseDTO;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemService {

    Map<String, String> buildErrorResponseForItemCreation(BindingResult bindingResult);

    ItemResponseDTO createItem(ItemRequestDTO itemRequestDTO, User seller);

    boolean isItemSellable(Item item);

    List<ItemListDTO> listItems();

    Item getItemById(UUID id);

    void saveLastBid(Item item, int bidAmount);

    void makeNotSellable(Item item);

    ItemCommonResponseDTO showItemDetails(UUID id);

    Item saveItem(Item item);

    Page<Item> getItemsBySellableTrueAndPage(int page);

    ResponseEntity<?> getItemsPaged(int page);
}
