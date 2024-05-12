package com.pc.greenbay.service;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.ItemRepository;
import com.pc.greenbay.repository.PurchaseRepository;
import com.pc.greenbay.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTests {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Item item;
    private Purchase purchase;

    @BeforeEach
    void setup(){

        UUID buyerId = UUID.randomUUID();
        User buyer = User.builder()
                .id(buyerId)
                .username("user1")
                .password("u12345")
                .balance(100)
                .roles("ROLE_USER")
                .build();

        UUID sellerId = UUID.randomUUID();
        User seller = User.builder()
                .id(sellerId)
                .username("user2")
                .password("u23456")
                .balance(10)
                .roles("ROLE_USER")
                .build();

        UUID itemId = UUID.randomUUID();
        item = Item.builder()
                .id(itemId)
                .name("Lenovo")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(10)
                .purchasePrice(50)
                .lastBid(0)
                .seller(seller)
                .build();

        purchase = Purchase.builder()
                .id(1L)
                .purchaseAmount(60)
                .buyer(buyer)
                .item(item)
                .build();
    }

    @Test
    @DisplayName("JUnit test for save purchase operation")
    void givenPurchaseObject_whenSavePurchase_thenVerifyMethodCall() {

        given(purchaseRepository.save(purchase)).willReturn(purchase);

        Purchase savedPurchase = purchaseService.savePurchase(purchase);

        assertThat(savedPurchase).isNotNull();
        assertThat(savedPurchase).isEqualTo(purchase);

        verify(purchaseRepository, times(1)).save(purchase);
    }

    @Test
    @DisplayName("JUnit test for get purchase by item operation")
    void givenPurchaseObject_whenFindByItem_thenReturnPurchaseObject() {

        given(purchaseRepository.findByItem(item)).willReturn(Optional.of(purchase));

        Purchase savedPurchase = purchaseService.getPurchaseByItem(item).get();

        assertThat(savedPurchase).isNotNull();
        assertThat(savedPurchase).isEqualTo(purchase);

        verify(purchaseRepository, times(1)).findByItem(item);
    }

    @Test
    @DisplayName("JUnit test for delete purchase operation")
    void givenPurchaseId_whenDeletePurchase_thenNothing() throws Exception {

        Long purchaseId = 1L;
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(purchase));
        willDoNothing().given(purchaseRepository).delete(purchase);

        purchaseService.deletePurchase(purchaseId);

        verify(purchaseRepository, times(1)).delete(purchase);
    }

    @Test
    @DisplayName("JUnit test for delete purchase operation when database failure")
    void givenPurchaseId_whenDeletePurchase_thenException() {

        Long purchaseId = 1L;
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(purchase));
        doThrow(new DataAccessException("Simulated database failure") {
        }).when(purchaseRepository).delete(any(Purchase.class));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            purchaseService.deletePurchase(purchaseId);
        });
        assertThat(exception.getMessage()).isEqualTo("Database error occurred while deleting purchase. The operation has failed.");

        verify(purchaseRepository).delete(any(Purchase.class));
    }
}
