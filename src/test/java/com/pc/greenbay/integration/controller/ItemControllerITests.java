package com.pc.greenbay.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.model.request.ItemRequestDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.ItemRepository;
import com.pc.greenbay.service.*;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ItemControllerITests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private BidService bidService;
    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper om;

    private User seller;
    private Item item;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup(){
        UUID userId = UUID.randomUUID();
        seller = User.builder()
                .id(userId)
                .username("user1")
                .password(userService.encodePassword("u12345"))
                .balance(100.0)
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
    }

@Test
@Transactional
@DisplayName("Integration test for create item method when valid input given")
void givenValidInput_whenCreateItem_thenReturnItemDTO() throws Exception {
    String authorizedUser = "Bearer ";
    authorizedUser += jwtService.generateToken("user1");

    ItemRequestDTO itemRequestDTO = new ItemRequestDTO("Lenovo", "tablet", "/img/green_fox_logo.png", 10, 50);

    ResultActions response = mockMvc.perform(post("/api/item")
            .header("authorization", authorizedUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(itemRequestDTO)));

    response.andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lenovo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("tablet"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.photoURL").value("/img/green_fox_logo.png"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.starting_price").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.purchase_price").value(50));
    }

@Test
@Transactional
@DisplayName("Integration test for create item method when invalid price given")
void givenInvalidPrice_whenCreateItem_thenReturnError() throws Exception {

    String authorizedUser = "Bearer ";
    authorizedUser += jwtService.generateToken("user1");

    ItemRequestDTO itemRequestDTO = new ItemRequestDTO("Lenovo", "tablet", "/img/green_fox_logo.png", 0, 0);

    ResultActions response = mockMvc.perform(post("/api/item")
            .header("authorization", authorizedUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(itemRequestDTO)));

    response.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.startingPrice").value("Starting price must be greater than or equal to 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.purchasePrice").value("Purchase price must be greater than or equal to 1"));
    }

@Test
@Transactional
@DisplayName("Integration test for create item method when missing name & description")
void givenMissingNameAndDescription_whenCreateItem_thenReturnError() throws Exception {

    String authorizedUser = "Bearer ";
    authorizedUser += jwtService.generateToken("user1");

    ItemRequestDTO itemRequestDTO = new ItemRequestDTO("", "", "/img/green_fox_logo.png", 0, 0);

    ResultActions response = mockMvc.perform(post("/api/item")
            .header("authorization", authorizedUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(itemRequestDTO)));

    response.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Item name is empty or missing."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Item description is empty or missing."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for show item details method when valid input given")
    void givenValidInput_whenShowItemDetails_thenReturnItemDetails() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        UUID bidderId = UUID.randomUUID();
        User bidder = User.builder()
                .id(bidderId)
                .username("user2")
                .password(userService.encodePassword("u23456"))
                .balance(200.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(bidder);

        UUID itemId = item.getId();

        Bid bid1 = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(20)
                .build();
        bidService.saveBid(bid1);

        Bid bid2 = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(30)
                .build();
        bidService.saveBid(bid2);

        Bid bid3 = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(40)
                .build();
        bidService.saveBid(bid3);

        ResultActions response = mockMvc.perform(get("/api/item/{id}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lenovo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("tablet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photoURL").value("/img/green_fox_logo.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value(seller.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidList", Matchers.hasSize(3)));
    }
    @Test
    @Transactional
    @DisplayName("Integration test for show item details method when invalid input given")
    void givenInvalidInput_whenShowItemDetails_thenReturnErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        ResultActions response = mockMvc.perform(get("/api/item/{id}", UUID.randomUUID())
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Item not found."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for show item details method when valid input & not sellable")
    void givenValidInputNotSellable_whenShowItemDetails_thenReturnItemDetails() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        item.setLastBid(51);
        item.setSellable(false);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        UUID buyerId = UUID.randomUUID();
        User buyer = User.builder()
                .id(buyerId)
                .username("user2")
                .password(userService.encodePassword("u23456"))
                .balance(149.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(buyer);

        Purchase purchase = Purchase.builder()
                .id(1L)
                .item(item)
                .buyer(buyer)
                .purchaseAmount(51)
                .build();
        purchaseService.savePurchase(purchase);

        ResultActions response = mockMvc.perform(get("/api/item/{id}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lenovo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("tablet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photoURL").value("/img/green_fox_logo.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller").value(seller.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer").value(buyer.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buying_price").value(51));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for show item details method when valid input & not sellable & no purchase")
    void givenItemNotSellableButNoPurchase_whenShowItemDetails_thenReturnErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        item.setLastBid(51);
        item.setSellable(false);
        itemService.saveItem(item);
        UUID itemId = item.getId();

        ResultActions response = mockMvc.perform(get("/api/item/{id}", itemId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Purchase record not found."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for pagination method")
    void givenValidInput_whenListItemsPages_thenReturnPageList() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        UUID item2Id = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(item2Id)
                .name("iPhone")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item2);

        UUID item3Id = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(item3Id)
                .name("iPad")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(250)
                .lastBid(151)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item3);

        UUID item4Id = UUID.randomUUID();
        Item item4 = Item.builder()
                .id(item4Id)
                .name("MacBook")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(1000)
                .purchasePrice(1150)
                .lastBid(50)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item4);

        ResultActions response = mockMvc.perform(get("/api/item")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(3));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for pagination method when requesting page 2")
    void givenValidInputPageTwo_whenListItemsPages_thenReturnPageList() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        UUID item2Id = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(item2Id)
                .name("iPhone")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item2);

        UUID item3Id = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(item3Id)
                .name("iPad")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(250)
                .lastBid(151)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item3);

        UUID item4Id = UUID.randomUUID();
        Item item4 = Item.builder()
                .id(item4Id)
                .name("MacBook")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(1000)
                .purchasePrice(1150)
                .lastBid(50)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item4);

        ResultActions response = mockMvc.perform(get("/api/item")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "2"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for pagination method when given invalid parameter")
    void givenInvalidParameter_whenListItemsPages_thenReturnErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        item.setLastBid(41);

        UUID item2Id = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(item2Id)
                .name("iPhone")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item2);

        UUID item3Id = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(item3Id)
                .name("iPad")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(250)
                .lastBid(151)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item3);

        UUID item4Id = UUID.randomUUID();
        Item item4 = Item.builder()
                .id(item4Id)
                .name("MacBook")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(1000)
                .purchasePrice(1150)
                .lastBid(50)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item4);

        ResultActions response = mockMvc.perform(get("/api/item")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "4"));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("There is no page: 4"));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper show method")
    void givenValidInput_whenShow_thenReturnItemList() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        UUID item2Id = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(item2Id)
                .name("iPhone")
                .description("mobile phone")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item2);

        UUID item3Id = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(item3Id)
                .name("iPad")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(250)
                .lastBid(151)
                .sellable(true)
                .seller(seller)
                .build();
        itemService.saveItem(item3);

        UUID item4Id = UUID.randomUUID();
        Item item4 = Item.builder()
                .id(item4Id)
                .name("MacBook")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(1000)
                .purchasePrice(1150)
                .lastBid(50)
                .sellable(false)
                .seller(seller)
                .build();
        itemService.saveItem(item4);

        ResultActions response = mockMvc.perform(get("/api/temp/show-all-items")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }
}
