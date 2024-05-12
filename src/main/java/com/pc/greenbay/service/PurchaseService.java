package com.pc.greenbay.service;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;

import java.util.Optional;

public interface PurchaseService {

    Purchase savePurchase(Purchase purchase);

    Optional<Purchase> getPurchaseByItem(Item item);

    void deletePurchase(Long id);
}
