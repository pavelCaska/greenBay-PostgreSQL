package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User seller;
    private Item item;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("u12345"))
                .balance(100)
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
                .sellable(true)
                .seller(seller)
                .build();
        itemRepository.save(item);
    }

    @Test
    @DisplayName("JUnit test save item operation")
    void givenItemObject_whenSave_thenReturnSavedItem() {
        Item toBeSavedItem = Item.builder()
                .name("Apple")
                .description("iPod")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(500)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();

        Item savedItem = itemRepository.save(toBeSavedItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo(toBeSavedItem.getName());
        assertThat(savedItem.getDescription()).isEqualTo(toBeSavedItem.getDescription());
        assertThat(savedItem.getPhotoURL()).isEqualTo(toBeSavedItem.getPhotoURL());
        assertThat(savedItem.getStartingPrice()).isEqualTo(toBeSavedItem.getStartingPrice());
        assertThat(savedItem.getPurchasePrice()).isEqualTo(toBeSavedItem.getPurchasePrice());
        assertThat(savedItem.getLastBid()).isEqualTo(toBeSavedItem.getLastBid());
        assertThat(savedItem.getSeller()).isEqualTo(toBeSavedItem.getSeller());

    }
    @Test
    @DisplayName("JUnit test for findItemById operation")
    void givenItemObject_whenFindItemById_thenReturnItemObject() {

        Optional<Item> optionalItem = itemRepository.findItemById(item.getId());

        assertThat(optionalItem.isPresent()).isTrue();
        assertThat(optionalItem.get()).isEqualTo(item);

    }
    @Test
    @DisplayName("JUnit test for getting all sellable items ")
    void givenItemObjects_whenFindAllBySellableTrue_thenReturnPage() {
        Item item2 = Item.builder()
                .name("Samsung")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemRepository.save(item2);

        Item item3 = Item.builder()
                .name("Lenovo")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(50)
                .purchasePrice(100)
                .lastBid(0)
                .seller(seller)
                .sellable(false)
                .build();
        itemRepository.save(item3);

        Page<Item> itemPage = itemRepository.findAllBySellableTrue(Pageable.unpaged());

        assertThat(itemPage).isNotNull();
        assertThat(itemPage).size().isEqualTo(2);
        assertThat(itemPage.getContent()).contains(item, item2);
    }
}
