package com.pc.greenbay.controller;

import com.pc.greenbay.model.ErrorDTO;
import com.pc.greenbay.model.request.ItemRequestDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.service.ItemService;
import com.pc.greenbay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping("/item")
    public ResponseEntity<?> createItem(
            @Valid @RequestBody ItemRequestDTO itemRequestDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(itemService.buildErrorResponseForItemCreation(bindingResult));
        }

        User seller = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(itemService.createItem(itemRequestDTO, seller));
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<?> showItemDetails(@PathVariable UUID id) {
            return ResponseEntity.ok(itemService.showItemDetails(id));
    }

    @GetMapping("/item")
    public ResponseEntity<?> listItemsPages(@RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        if(page > 0) {
            return itemService.getItemsPaged(page);
        }
        return ResponseEntity.badRequest().body(new ErrorDTO("Invalid parameters"));
    }

    @GetMapping("/temp/show-all-items")
    public ResponseEntity<?> showAllItems() {
        return ResponseEntity.ok(itemService.listItems());
    }
}
