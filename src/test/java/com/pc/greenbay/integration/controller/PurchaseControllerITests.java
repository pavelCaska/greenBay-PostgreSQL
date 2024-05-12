package com.pc.greenbay.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.entity.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PurchaseControllerITests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper om;

    private Purchase purchase;

    @BeforeEach
    void setup() {

        UUID adminId = UUID.randomUUID();
        User admin = User.builder()
                .id(adminId)
                .username("admin")
                .password(userService.encodePassword("A12345"))
                .balance(0.0)
                .roles("ROLE_ADMIN")
                .build();
        userService.saveUser(admin);

        UUID sellerId = UUID.randomUUID();
        User seller = User.builder()
                .id(sellerId)
                .username("user1")
                .password(userService.encodePassword("u12345"))
                .balance(100.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(seller);

        UUID itemId = UUID.randomUUID();
        Item item = Item.builder()
                .id(itemId)
                .name("Lenovo")
                .description("tablet")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(10)
                .purchasePrice(50)
                .lastBid(51)
                .sellable(false)
                .seller(seller)
                .build();
        itemService.saveItem(item);

        UUID buyerId = UUID.randomUUID();
        User buyer = User.builder()
                .id(buyerId)
                .username("user2")
                .password(userService.encodePassword("u23456"))
                .balance(149.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(buyer);

        purchase = Purchase.builder()
                .id(1L)
                .item(item)
                .buyer(buyer)
                .purchaseAmount(51)
                .build();
        purchaseService.savePurchase(purchase);
    }

    @Test
    @Transactional
    @DisplayName("Integration test for delete purchase method")
    void givenValidPurchaseId_whenShowDeletePurchase_thenDeletePurchase() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        Long purchaseId = purchase.getId();

        ResultActions response = mockMvc.perform(delete("/api/purchase/{id}", purchaseId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("Integration test for delete purchase method")
    void givenInvalidPurchaseId_whenShowDeletePurchase_thenException() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        long purchaseId = purchase.getId() + 1;

        ResultActions response = mockMvc.perform(delete("/api/purchase/{id}", purchaseId)
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Purchase not found."));
    }
}