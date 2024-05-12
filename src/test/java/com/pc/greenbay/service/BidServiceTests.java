package com.pc.greenbay.service;

import com.pc.greenbay.exception.*;
import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.model.response.BidListDTO;
import com.pc.greenbay.model.response.BidPlacedResponseDTO;
import com.pc.greenbay.model.response.ItemBoughtResponseDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.BidRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BidServiceTests {

    @Mock
    private BidRepository bidRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private PurchaseService purchaseService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BidServiceImpl bidService;

    private Item item;
    private User seller;
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

        UUID bidderId = UUID.randomUUID();
        bidder = User.builder()
                .id(bidderId)
                .username("user2")
                .password("u23456")
                .balance(100)
                .roles("ROLE_USER")
                .build();
    }

    @Test
    @DisplayName("JUnit test for place bid method when bidder has no greenBay dollars")
    void givenValidInput_whenPlaceBid_thenThrowNoMoneyException() throws Exception {

        bidder.setBalance(0);
        int bidAmount = 49;

//        given(itemService.getItemById(item.getId())).willReturn(item);

        NoMoneyException exception = assertThrows(NoMoneyException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("You have no greenBay dollars, you can't bid.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("You have no greenBay dollars, you can't bid.");

        verify(itemService, times(0)).getItemById(item.getId());
    }

    @Test
    @DisplayName("JUnit test for place bid method when item not found")
    void givenValidInput_whenPlaceBid_thenItemNotFoundException() throws Exception {

        UUID invalidItemId = UUID.randomUUID();

        int bidAmount = 49;
        given(itemService.getItemById(invalidItemId)).willThrow(new RecordNotFoundException("Item not found."));

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            bidService.placeBid(invalidItemId, bidder, bidAmount);
        });

//        assertEquals("Item not found.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Item not found.");

        verify(itemService, times(1)).getItemById(invalidItemId);
    }

    @Test
    @DisplayName("JUnit test for place bid method when bidder equals seller")
    void givenValidInput_whenPlaceBid_thenThrowBidderIsSellerException() throws Exception {

        bidder = seller;
        int bidAmount = 49;

        given(itemService.getItemById(item.getId())).willReturn(item);

        BidOnOwnItemException exception = assertThrows(BidOnOwnItemException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("You cannot bid on your own item.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("You cannot bid on your own item.");

        verify(itemService, times(1)).getItemById(item.getId());
    }

    @Test
    @DisplayName("JUnit test for place bid method when item is not sellable")
    void givenValidInput_whenPlaceBid_thenThrowNotSellableException() throws Exception {

        item.setSellable(false);
        int bidAmount = 49;

        given(itemService.getItemById(item.getId())).willReturn(item);

        ItemNotSellableException exception = assertThrows(ItemNotSellableException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("Item is not sellable.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Item is not sellable.");

        verify(itemService, times(1)).getItemById(item.getId());
    }

    @Test
    @DisplayName("JUnit test for place bid method when bidder has not enough money")
    void givenValidInput_whenPlaceBid_thenThrowNotEnoughMoneyException() throws Exception {

        bidder.setBalance(9);
        int bidAmount = 49;

//        given(itemService.getItemById(item.getId())).willReturn(item);

        NotEnoughMoneyException exception = assertThrows(NotEnoughMoneyException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("You have not enough greenBay dollars on your account.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("You have not enough greenBay dollars on your account.");

        verify(itemService, times(0)).getItemById(item.getId());
    }

    @Test
    @DisplayName("JUnit test for place bid method when bid is too low")
    void givenValidInput_whenPlaceBid_thenThrowLowBidException() throws Exception {

        item.setLastBid(11);
        int bidAmount = 11;

        given(itemService.getItemById(item.getId())).willReturn(item);

        LowBidException exception = assertThrows(LowBidException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("Your bid is too low.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Your bid is too low.");

        verify(itemService, times(1)).getItemById(item.getId());
    }

    @Test
    @DisplayName("JUnit test for place new bid method")
    void givenValidInput_whenPlaceBid_thenSaveBidObjectAndReturnDTO() throws Exception {

        item.setLastBid(20);
        int bidAmount = 25;

        Bid bid = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        given(itemService.getItemById(item.getId())).willReturn(item);

        BidPlacedResponseDTO bidPlacedResponseDTO = null;
        bidPlacedResponseDTO = (BidPlacedResponseDTO) bidService.placeBid(item.getId(), bidder, bidAmount);

        assertThat(bidPlacedResponseDTO).isNotNull();
        assertThat(bidPlacedResponseDTO.getBidAmount()).isEqualTo(bidAmount);
        assertThat(bidPlacedResponseDTO.getName()).isEqualTo(item.getName());
        assertThat(bidPlacedResponseDTO.getSellerUsername()).isEqualTo(item.getSeller().getUsername());

        verify(itemService, times(1)).getItemById(item.getId());
        verify(itemService, times(1)).saveLastBid(item, bidAmount);
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    @DisplayName("JUnit test for simulating database failure while saving new bid")
    void givenValidInput_whenPlaceBid_thenSavingBidThrowException() throws Exception {

        item.setLastBid(20);
        int bidAmount = 25;

        Bid bid = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        given(itemService.getItemById(item.getId())).willReturn(item);
        given(bidRepository.save(any(Bid.class))).willThrow(new DataAccessException("Simulated database failure") {
        });

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("Database error occurred while placing your bid. The operation has failed.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Database error occurred while placing your bid. The operation has failed.");

        verify(itemService, times(1)).getItemById(item.getId());
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    @DisplayName("JUnit test for place bid and purchase operation")
    void givenValidInput_whenPlaceBid_thenPurchaseItemAndReturnDTO() throws Exception {

        UUID itemId = item.getId();
        item.setLastBid(20);
        int bidAmount = 51;

        Bid bid = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        User buyer = bidder;

        Purchase purchase = Purchase.builder()
                .id(1L)
                .item(item)
                .buyer(buyer)
                .purchaseAmount(bidAmount)
                .build();

        given(itemService.getItemById(itemId)).willReturn(item);

        ItemBoughtResponseDTO itemBoughtResponseDTO = null;
        itemBoughtResponseDTO = (ItemBoughtResponseDTO) bidService.placeBid(itemId, bidder, bidAmount);

        assertThat(itemBoughtResponseDTO).isNotNull();
        assertThat(itemBoughtResponseDTO.getBuyingPrice()).isEqualTo(bidAmount);
        assertThat(itemBoughtResponseDTO.getName()).isEqualTo(item.getName());
        assertThat(itemBoughtResponseDTO.getSellerUsername()).isEqualTo(item.getSeller().getUsername());
        assertThat(itemBoughtResponseDTO.getBuyerUsername()).isEqualTo(bidder.getUsername());

        verify(itemService, times(1)).getItemById(itemId);
        verify(itemService, times(1)).saveLastBid(item, bidAmount);
        verify(itemService, times(1)).makeNotSellable(item);
        verify(bidRepository, times(1)).save(any(Bid.class));
        verify(purchaseService, times(1)).savePurchase(any(Purchase.class));
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("JUnit test for place bid operation while purchase has failed")
    void givenValidInput_whenPlaceBid_thenThrowPurchaseFailedException() throws Exception {

        item.setLastBid(20);
        int bidAmount = 51;

        Bid bid = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        User buyer = bidder;

        Purchase purchase = Purchase.builder()
                .id(1L)
                .item(item)
                .buyer(buyer)
                .purchaseAmount(bidAmount)
                .build();

        given(itemService.getItemById(item.getId())).willReturn(item);
        given(purchaseService.savePurchase(any(Purchase.class))).willThrow(new DataAccessException("Simulated database failure") {
        });

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            bidService.placeBid(item.getId(), bidder, bidAmount);
        });

//        assertEquals("Purchase has failed.", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Database error occurred while saving your purchase. The operation has failed.");

        verify(itemService, times(1)).getItemById(item.getId());
        verify(itemService, times(1)).saveLastBid(item, bidAmount);
        verify(itemService, times(1)).makeNotSellable(item);
        verify(bidRepository, times(1)).save(any(Bid.class));
        verify(purchaseService, times(1)).savePurchase(any(Purchase.class));
        verify(userService, times(0)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("JUnit test for find bids by item method")
    void givenItemObject_whenFindBidsByItem_thenReturnDTOList() throws Exception {

        item.setLastBid(40);

        Bid bid1 = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(20)
                .build();

        Bid bid2 = Bid.builder()
                .id(2L)
                .item(item)
                .bidder(bidder)
                .bidAmount(30)
                .build();

        Bid bid3 = Bid.builder()
                .id(3L)
                .item(item)
                .bidder(bidder)
                .bidAmount(40)
                .build();

        List<Bid> bidList = List.of(bid1, bid2, bid3);

        given(bidRepository.findAllByItem(item)).willReturn(bidList);

        List<BidListDTO> bidListDTOList = null;
        bidListDTOList = bidService.findBidsByItem(item);

        assertThat(bidListDTOList).isNotNull();
        assertThat(bidListDTOList.size()).isEqualTo(3);
        assertThat(bidListDTOList.get(0).getBidAmount()).isEqualTo(20);
        assertThat(bidListDTOList.get(1).getBidAmount()).isEqualTo(30);
        assertThat(bidListDTOList.get(2).getBidAmount()).isEqualTo(40);
    }
}