package com.pc.greenbay.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.model.request.BidRequestDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.ItemRepository;
import com.pc.greenbay.repository.UserRepository;
import com.pc.greenbay.service.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BidControllerITests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BidService bidService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper om;

    private User seller;
    private Item item;
    private User bidder;

    @BeforeEach
    void setup() {
        UUID sellerId = UUID.randomUUID();
        seller = User.builder()
                .id(sellerId)
                .username("user1")
                .password("u12345")
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(seller);

        UUID itemId = UUID.randomUUID();
        item = Item.builder()
                .id(itemId)
                .name("Lenovo")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(10)
                .purchasePrice(50)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item);

        UUID bidderId = UUID.randomUUID();
        bidder = User.builder()
                .id(bidderId)
                .username("user2")
                .password("u23456")
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(bidder);

    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when bidder has no greenBay dollars")
    void givenValidInput_whenPlaceBid_thenThrowNoMoneyException() throws Exception {

        bidder.setBalance(0);
        userService.saveUser(bidder);

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        UUID itemId = item.getId();

        BidRequestDTO bidRequestDTO = new BidRequestDTO(49);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("You have no greenBay dollars, you can't bid."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when item not found")
    void givenValidInput_whenPlaceBid_thenItemNotFoundException() throws Exception {
        UUID invalidItemId = UUID.randomUUID();

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(49);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", invalidItemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Item not found."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when bidder equals seller")
    void givenValidInput_whenPlaceBid_thenThrowBidderIsSellerException() throws Exception {

        UUID itemId = item.getId();
        bidder = seller;
        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(49);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("You cannot bid on your own item."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when item is not sellable")
    void givenValidInput_whenPlaceBid_thenThrowNotSellableException() throws Exception {

        item.setSellable(false);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(49);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Item is not sellable."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when bidder has not enough money")
    void givenValidInput_whenPlaceBid_thenThrowNotEnoughMoneyException() throws Exception {

        UUID itemId = item.getId();
        bidder.setBalance(9);
        userService.saveUser(bidder);

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(49);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("You have not enough greenBay dollars on your account."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when bid is too low")
    void givenValidInput_whenPlaceBid_thenThrowBitTooLowException() throws Exception {

        item.setLastBid(11);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(11);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Your bid is too low."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place new bid operation")
    void givenValidInput_whenPlaceBid_thenSaveBidAndReturnDTO() throws Exception {

        item.setLastBid(20);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(25);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lenovo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("tablet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photoURL").value("/img/green_fox_logo.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bid_placed").value("25"));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for place bid operation when item has been purchased")
    void givenValidInput_whenPlaceBid_thenPurchaseItemAndReturnDTO() throws Exception {

        item.setLastBid(20);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user2");

        BidRequestDTO bidRequestDTO = new BidRequestDTO(51);

        ResultActions response = mockMvc.perform(post("/api/bid/{itemId}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bidRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lenovo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("tablet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photoURL").value("/img/green_fox_logo.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bought_at").value("51"));
    }
}
