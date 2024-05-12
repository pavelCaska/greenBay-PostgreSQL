package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.entity.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@DataJpaTest
@Import({BCryptPasswordEncoder.class})
public class PurchaseRepositoryTests {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User buyer;
    private Item item;
    private Purchase purchase;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("u12345"))
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userRepository.save(buyer);

        User seller = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("u23456"))
                .balance(10)
                .roles("ROLE_USER")
                .build();
        userRepository.save(seller);

        item = Item.builder()
                .name("Lenovo")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(10)
                .purchasePrice(50)
                .lastBid(0)
                .seller(seller)
                .build();
        itemRepository.save(item);

        purchase = Purchase.builder()
                .purchaseAmount(60)
                .buyer(buyer)
                .item(item)
                .build();
        purchaseRepository.save(purchase);
    }

    @Test
    @DisplayName("JUnit test for save purchase operation")
    void givenPurchaseObject_whenSave_thenReturnSavedPurchase() {

        Purchase toBeSavedPurchase = Purchase.builder()
                .purchaseAmount(60)
                .buyer(buyer)
                .item(item)
                .build();

        Purchase savedPurchase = purchaseRepository.save(toBeSavedPurchase);

        assertThat(savedPurchase).isNotNull();
        assertThat(savedPurchase.getId()).isNotNull();
        assertThat(savedPurchase.getPurchaseAmount()).isEqualTo(60);
        assertThat(savedPurchase.getBuyer()).isEqualTo(buyer);
        assertThat(savedPurchase.getItem()).isEqualTo(item);
    }

    @Test
    @DisplayName("JUnit test for delete purchase operation")
    void givenPurchaseObject_whenDelete_thenRemovePurchase() {

        Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchase.getId());
        assertThat(purchaseOptional.isPresent()).isTrue();

        purchaseRepository.delete(purchase);

        Optional<Purchase> optionalPurchase = purchaseRepository.findById(purchase.getId());
        assertThat(optionalPurchase).isEmpty();

    }

    @Test
    @DisplayName("JUnit test for findByItem operation")
    void givenPurchaseObject_whenFindByItem_thenReturnPurchaseObject() {

        Optional<Purchase> optionalPurchase = purchaseRepository.findByItem(item);

        assertThat(optionalPurchase).isNotEmpty();
        assertThat(optionalPurchase.get().getItem()).isEqualTo(item);
    }
}
