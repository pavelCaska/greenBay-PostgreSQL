package com.pc.greenbay.controller;

import com.pc.greenbay.model.request.BidRequestDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.service.BidService;
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
public class BidController {

    private final BidService bidService;
    private final UserService userService;

    @Autowired
    public BidController(BidService bidService, UserService userService) {
        this.bidService = bidService;
        this.userService = userService;
    }
    @PostMapping("/bid/{itemId}")
    public ResponseEntity<?> placeBidPost(@PathVariable UUID itemId,
                                          @Valid @RequestBody BidRequestDTO bidRequestDTO, BindingResult bindingResult,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bidService.buildErrorResponseForBiding(bindingResult));
        }

        User bidder = userService.findByUsername(userDetails.getUsername());

            return ResponseEntity.ok(bidService.placeBid(itemId, bidder, bidRequestDTO.getBidAmount()));
    }
}
