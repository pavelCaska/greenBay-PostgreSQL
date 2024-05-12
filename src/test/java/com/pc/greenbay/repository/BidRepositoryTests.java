package com.pc.greenbay.repository;

import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
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

import java.util.List;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
public class BidRepositoryTests {

    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User bidder;
    private User seller;
    private Item item;

    @BeforeEach
    void setUp() {
        bidder = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("u12345"))
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userRepository.save(bidder);

        seller = User.builder()
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
    }

    @Test
    @DisplayName("JUnit test save bid operation")
    void givenBidObject_whenSave_thenReturnSavedBid() {

        Bid bid = Bid.builder()
                .bidAmount(20)
                .bidder(bidder)
                .item(item)
                .build();

        Bid savedBid = bidRepository.save(bid);

        assertThat(savedBid).isNotNull();
        assertThat(savedBid.getId()).isNotNull();
        assertThat(savedBid.getBidder()).isEqualTo(bidder);
        assertThat(savedBid.getItem()).isEqualTo(item);
        assertThat(savedBid.getBidAmount()).isEqualTo(bid.getBidAmount());

    }
    @Test
    @DisplayName("JUnit test for findAllByItem operation")
    void givenBidList_whenFindAllByItem_thenBidList() {

        Bid bid = Bid.builder()
                .bidAmount(20)
                .bidder(bidder)
                .item(item)
                .build();
        bidRepository.save(bid);

        User bidder2 = User.builder()
                .username("user3")
                .password(passwordEncoder.encode("u34567"))
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userRepository.save(bidder2);

        Bid bid2 = Bid.builder()
                .bidAmount(30)
                .bidder(bidder2)
                .item(item)
                .build();
        bidRepository.save(bid2);

        User bidder3 = User.builder()
                .username("user4")
                .password(passwordEncoder.encode("u45678"))
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userRepository.save(bidder3);

        Item item2 = Item.builder()
                .name("Asus")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(75)
                .purchasePrice(99)
                .lastBid(0)
                .seller(seller)
                .build();
        itemRepository.save(item2);

        Bid bid3 = Bid.builder()
                .bidAmount(76)
                .bidder(bidder3)
                .item(item2)
                .build();
        bidRepository.save(bid3);

        List<Bid> bidList = bidRepository.findAllByItem(item);

        assertThat(bidList).isNotNull();
        assertThat(bidList.size()).isEqualTo(2);
        assertThat(bidList).contains(bid, bid2);
    }
}
