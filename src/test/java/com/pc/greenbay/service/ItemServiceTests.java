package com.pc.greenbay.service;

import com.pc.greenbay.exception.RecordNotFoundException;
import com.pc.greenbay.entity.Bid;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import com.pc.greenbay.model.request.ItemRequestDTO;
import com.pc.greenbay.model.response.*;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.ItemRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private PurchaseService purchaseService;
    @Mock
    private BidService bidService;
    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private User seller;

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
    }

    @Test
    @DisplayName("JUnit test for save item")
    void givenItemObject_whenSaveItem_thenReturnItemObject() {
        given(itemRepository.save(item)).willReturn(item);

        Item savedItem = itemService.saveItem(item);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem).isEqualTo(item);
        assertThat(savedItem.getId()).isEqualTo(item.getId());

    }

    @Test
    @DisplayName("JUnit test for save lastBid method")
    void givenItemObject_whenSaveLastBid_thenUpdateItemObject() {
        int lastBid = 150;

        itemService.saveLastBid(item, lastBid);

        assertEquals(lastBid, item.getLastBid());
        verify(itemRepository, Mockito.times(1)).save(item);
    }
    @Test
    @DisplayName("JUnit test for return boolean value method")
    void givenItemObject_whenIsSellable_thenReturnBoolean() {

        boolean result = itemService.isItemSellable(item);

        assertTrue(result);
    }

    @Test
    @DisplayName("JUnit test for set sellable to false method")
    void givenItemObject_whenMakeNotSellable_thenUpdateItemObject() {

        itemService.makeNotSellable(item);

        assertFalse(item.isSellable());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("JUnit test for get item by id method")
    void givenItemId_whenGetItemById_thenReturnItemObject() {
        UUID uuid = item.getId();
        given(itemRepository.findItemById(uuid)).willReturn(Optional.of(item));

        Item savedItem = itemService.getItemById(uuid);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem).isEqualTo(item);
    }

    @Test
    @DisplayName("JUnit test for get sellable items paged method")
    void givenItemList_whenGetItemBySellableAndPage_thenReturnItemPage() {

        UUID itemId2 = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(itemId2)
                .name("Asus")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .seller(seller)
                .build();
        UUID itemId3 = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(itemId3)
                .name("Apple")
                .description("iPad")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(200)
                .purchasePrice(250)
                .lastBid(0)
                .seller(seller)
                .build();

        int page = 0;
        Pageable pageable = PageRequest.of(page, 3);
        given(itemRepository.findAllBySellableTrue(pageable)).willReturn(new PageImpl<>(List.of(item, item2, item3)));

        Page<Item>  itemPage = itemService.getItemsBySellableTrueAndPage(page);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getContent()).containsExactly(item, item2, item3);
    }

    @Test
    @DisplayName("JUnit test for list items method")
    void givenItemList_whenListItems_thenJsonList() {

        UUID itemId2 = UUID.randomUUID();
        Item item2 = Item.builder()
                .id(itemId2)
                .name("Asus")
                .description("notebook")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(100)
                .purchasePrice(150)
                .lastBid(0)
                .seller(seller)
                .build();
        UUID itemId3 = UUID.randomUUID();
        Item item3 = Item.builder()
                .id(itemId3)
                .name("Apple")
                .description("iPad")
                .photoURL("/img/green_fox_logo.png")
                .startingPrice(200)
                .purchasePrice(250)
                .lastBid(0)
                .seller(seller)
                .build();
        given(itemRepository.findAll()).willReturn(List.of(item, item2, item3));

        List<ItemListDTO> jsonList = itemService.listItems();

        assertThat(jsonList).isNotNull();
        assertThat(jsonList.size()).isEqualTo(3);
        assertThat(jsonList.get(0).getId()).isEqualTo(item.getId());
        assertThat(jsonList.get(1).getId()).isEqualTo(item2.getId());
        assertThat(jsonList.get(2).getId()).isEqualTo(item3.getId());
    }

    @Test
    @DisplayName("JUnit test for create item method")
    void givenValidItemRequest_whenCreateItem_thenItemResponseDTO() throws Exception {

        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("Apple", "iPad", "/img/green_fox_logo.png", 200, 250);
        UUID itemToSaveId = UUID.randomUUID();
        Item itemToSave = Item.builder()
                .id(itemToSaveId)
                .name(itemRequestDTO.getName())
                .description(itemRequestDTO.getDescription())
                .photoURL(itemRequestDTO.getPhotoURL())
                .sellable(true)
                .startingPrice(itemRequestDTO.getStartingPrice())
                .purchasePrice(itemRequestDTO.getPurchasePrice())
                .seller(seller)
                .build();

        UUID savedItemId = itemToSave.getId();
        given(itemRepository.save(any(Item.class))).willAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(savedItemId);
            return savedItem;
        });

        ItemResponseDTO itemResponseDTO = null;
            itemResponseDTO = itemService.createItem(itemRequestDTO, seller);

        assertThat(itemResponseDTO).isNotNull();
        assertThat(itemResponseDTO.getItemID()).isNotNull();
        assertThat(itemResponseDTO.getName()).isEqualTo(itemRequestDTO.getName());
        assertThat(itemResponseDTO.getDescription()).isEqualTo(itemRequestDTO.getDescription());
    }

//    @Test
//    @DisplayName("JUnit test for simulating database failure while saving item")
//    void givenDatabaseFailure_whenCreateItem_thenThrowException() throws Exception {
//
//        ItemRequestDTO itemRequestDTO = new ItemRequestDTO("Apple", "iPad", "/img/green_fox_logo.png", 200, 250);
//
//        given(itemRepository.save(any(Item.class))).willThrow(new DataAccessException("Simulated database failure") {});
//
//        assertThrows(DataAccessException.class, () -> {
//            itemService.createItem(itemRequestDTO, seller);
//        });
//
//        verify(itemRepository, times(1)).save(any(Item.class));
//    }

    @Test
    @DisplayName("JUnit test for show item details method when item not sellable")
    void givenValidItemIDAndNotSellable_whenShowItemDetails_thenReturnItemNotSellableResponseDTO() throws Exception {

        item.setSellable(false);
        UUID itemId = item.getId();

        given(itemRepository.findItemById(itemId)).willReturn(Optional.of(item));
        UUID buyerId = UUID.randomUUID();
        User buyer = User.builder()
                .id(buyerId)
                .username("user2")
                .password("u23456")
                .balance(100)
                .roles("ROLE_USER")
                .build();

        Purchase purchase = Purchase.builder()
                .id(1L)
                .purchaseAmount(60)
                .buyer(buyer)
                .item(item)
                .build();

        given(purchaseService.getPurchaseByItem(item)).willReturn(Optional.of(purchase));

        ItemNotSellableResponseDTO itemNotSellableResponseDTO = null;
        itemNotSellableResponseDTO = (ItemNotSellableResponseDTO) itemService.showItemDetails(itemId);

        assertThat(itemNotSellableResponseDTO).isNotNull();
        assertThat(itemNotSellableResponseDTO.getName()).isEqualTo(item.getName());
        assertThat(itemNotSellableResponseDTO.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemNotSellableResponseDTO.getSellerUsername()).isEqualTo(item.getSeller().getUsername());
        assertThat(itemNotSellableResponseDTO.getBuyerUsername()).isEqualTo(purchase.getBuyer().getUsername());
    }

    @Test
    @DisplayName("JUnit test for show item details method when itemId invalid")
    void givenInvalidItemId_whenShowItemDetails_thenThrowRecordNotFoundException() throws Exception {

        UUID invalidId = UUID.randomUUID();

        given(itemRepository.findItemById(invalidId)).willReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            itemService.showItemDetails(invalidId);
        });

        assertThat(exception.getMessage()).isEqualTo("Item not found.");
        verify(itemRepository, times(1)).findItemById(invalidId);

    }

    @Test
    @DisplayName("JUnit test for show item details method when purchase record not found")
    void givenValidItemId_whenShowItemDetails_thenThrowRecordNotFoundException() throws Exception {

        item.setSellable(false);
        UUID itemId = item.getId();

        given(itemRepository.findItemById(itemId)).willReturn(Optional.of(item));
        given(purchaseService.getPurchaseByItem(item)).willReturn(Optional.empty());


        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            itemService.showItemDetails(itemId);
        });
        assertThat(exception.getMessage()).isEqualTo("Purchase record not found.");

        verify(itemRepository, times(1)).findItemById(item.getId());
        verify(purchaseService, times(1)).getPurchaseByItem(item);
    }

    @Test
    @DisplayName("JUnit test for show item details method when item sellable")
    void givenValidItemIDAndSellable_whenShowItemDetails_thenReturnItemSellableResponseDTO() throws Exception {

        item.setLastBid(40);
        UUID itemId = item.getId();

        UUID bidderId = UUID.randomUUID();
        User bidder = User.builder()
                .id(bidderId)
                .username("user2")
                .password("u23456")
                .balance(100)
                .roles("ROLE_USER")
                .build();

        int bidAmount = 49;
        Bid bid = Bid.builder()
                .id(1L)
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        given(itemRepository.findItemById(itemId)).willReturn(Optional.of(item));
        List<BidListDTO> bidList = List.of(new BidListDTO(bid.getId(), bid.getBidder().getUsername(), bid.getBidAmount()));
        given(bidService.findBidsByItem(item)).willReturn(bidList);

        ItemSellableResponseDTO itemSellableResponseDTO = null;
        itemSellableResponseDTO = (ItemSellableResponseDTO) itemService.showItemDetails(itemId);

        assertThat(itemSellableResponseDTO).isNotNull();
        assertThat(itemSellableResponseDTO.getName()).isEqualTo(item.getName());
        assertThat(itemSellableResponseDTO.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemSellableResponseDTO.getSellerUsername()).isEqualTo(item.getSeller().getUsername());
        assertThat(itemSellableResponseDTO.getBidList()).isNotNull();
        assertThat(itemSellableResponseDTO.getBidList()).size().isEqualTo(1);
    }
}
