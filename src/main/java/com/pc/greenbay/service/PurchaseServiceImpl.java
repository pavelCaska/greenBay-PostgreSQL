package com.pc.greenbay.service;

import com.pc.greenbay.exception.RecordNotFoundException;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Purchase savePurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public Optional<Purchase> getPurchaseByItem(Item item) {
        return purchaseRepository.findByItem(item);
    }

    @Override
    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Purchase not found."));
        try {
//            User buyer = purchase.getBuyer();
//            buyer.getPurchases().remove(purchase);
            purchaseRepository.delete(purchase);
        } catch (DataAccessException e) {
            throw new DataAccessException("Database error occurred while deleting purchase. The operation has failed.") {
            };
        }
    }
}
