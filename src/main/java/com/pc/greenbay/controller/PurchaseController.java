package com.pc.greenbay.controller;

import com.pc.greenbay.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @DeleteMapping("/purchase/{id}")
    public ResponseEntity<?> deletePurchase(@PathVariable Long id) {
            purchaseService.deletePurchase(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
