package com.pc.greenbay.bootstrap;

import com.pc.greenbay.entity.Item;
import com.pc.greenbay.model.response.ItemListDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.UserRepository;
import com.pc.greenbay.service.ItemService;
import com.pc.greenbay.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class bootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;

    public bootstrapData(UserRepository userRepository, UserService userService, ItemService itemService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadUsers();
        loadItems();
    }

    public void loadUsers() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(userService.encodePassword("A12345"))
                    .balance(0)
                    .roles("ROLE_ADMIN")
                    .build();
            userService.saveUser(admin);

            User user1 = User.builder()
                    .username("user1")
                    .password(userService.encodePassword("u12345"))
                    .balance(100)
                    .roles("ROLE_USER")
                    .build();
            userService.saveUser(user1);

            User user2 = User.builder()
                    .username("user2")
                    .password(userService.encodePassword("u23456"))
                    .balance(200)
                    .roles("ROLE_USER")
                    .build();
            userService.saveUser(user2);

            User user3 = User.builder()
                    .username("user3")
                    .password(userService.encodePassword("u34567"))
                    .balance(300)
                    .roles("ROLE_USER")
                    .build();
            userService.saveUser(user3);

            User user4 = User.builder()
                    .username("user4")
                    .password(userService.encodePassword("u45678"))
                    .balance(400)
                    .roles("ROLE_USER")
                    .build();
            userService.saveUser(user4);
        }
    }

    public void loadItems() {
        List<ItemListDTO> itemList = itemService.listItems();
        List<User> userList = userRepository.findAll();
        if (itemList.isEmpty() && userList.size() > 4) {

            User user1 = userList.get(1);
            User user2 = userList.get(2);
            User user3 = userList.get(3);
            User user4 = userList.get(4);

            Item item1 = Item.builder()
                    .name("Motorola")
                    .description("mobile phone")
                    .photoURL("/img/green_fox_logo.png")
                    .startingPrice(75)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user1)
                    .build();
            itemService.saveItem(item1);

            Item item2 = Item.builder()
                    .name("HP Elite")
                    .description("notebook")
                    .photoURL("/img/samsung_logo.png")
                    .startingPrice(100)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user2)
                    .build();
            itemService.saveItem(item2);

            Item item3 = Item.builder()
                    .name("Apple Watch")
                    .description("smartwatch")
                    .photoURL("/img/apple_logo.png")
                    .startingPrice(100)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user3)
                    .build();
            itemService.saveItem(item3);

            Item item4 = Item.builder()
                    .name("Samsung TV")
                    .description("TV set")
                    .photoURL("/img/samsung_tv_logo.png")
                    .startingPrice(200)
                    .purchasePrice(350)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user4)
                    .build();
            itemService.saveItem(item4);

            Item item5 = Item.builder()
                    .name("Sony Headphones")
                    .description("wireless headphones")
                    .photoURL("/img/sony_logo.png")
                    .startingPrice(100)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user1)
                    .build();
            itemService.saveItem(item5);

            Item item6 = Item.builder()
                    .name("LG Monitor")
                    .description("computer monitor")
                    .photoURL("/img/lg_logo.png")
                    .startingPrice(125)
                    .purchasePrice(200)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user2)
                    .build();
            itemService.saveItem(item6);

            Item item7 = Item.builder()
                    .name("Google Pixel")
                    .description("smartphone")
                    .photoURL("/img/google_logo.png")
                    .startingPrice(100)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user3)
                    .build();
            itemService.saveItem(item7);

            Item item8 = Item.builder()
                    .name("Sony PlayStation")
                    .description("game console")
                    .photoURL("/img/sony_playstation_logo.png")
                    .startingPrice(50)
                    .purchasePrice(100)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user4)
                    .build();
            itemService.saveItem(item8);

            Item item9 = Item.builder()
                    .name("Nintendo Switch")
                    .description("game console")
                    .photoURL("/img/nintendo_logo.png")
                    .startingPrice(75)
                    .purchasePrice(125)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user1)
                    .build();
            itemService.saveItem(item9);

            Item item10 = Item.builder()
                    .name("Dell Laptop")
                    .description("notebook")
                    .photoURL("/img/dell_logo.png")
                    .startingPrice(75)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user2)
                    .build();
            itemService.saveItem(item10);

            Item item11 = Item.builder()
                    .name("Lenovo ThinkPad")
                    .description("notebook")
                    .photoURL("/img/lenovo_logo.png")
                    .startingPrice(150)
                    .purchasePrice(250)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user3)
                    .build();
            itemService.saveItem(item11);

            Item item12 = Item.builder()
                    .name("Microsoft Surface")
                    .description("notebook")
                    .photoURL("/img/microsoft_logo.png")
                    .startingPrice(200)
                    .purchasePrice(300)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user4)
                    .build();
            itemService.saveItem(item12);

            Item item13 = Item.builder()
                    .name("Apple iPhone")
                    .description("smartphone")
                    .photoURL("/img/apple_logo.png")
                    .startingPrice(150)
                    .purchasePrice(250)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user1)
                    .build();
            itemService.saveItem(item13);

            Item item14 = Item.builder()
                    .name("Samsung Galaxy")
                    .description("smartphone")
                    .photoURL("/img/samsung_logo.png")
                    .startingPrice(50)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user2)
                    .build();
            itemService.saveItem(item14);

            Item item15 = Item.builder()
                    .name("Sony Camera")
                    .description("digital camera")
                    .photoURL("/img/sony_logo.png")
                    .startingPrice(50)
                    .purchasePrice(100)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user3)
                    .build();
            itemService.saveItem(item15);

            Item item16 = Item.builder()
                    .name("Canon Camera")
                    .description("digital camera")
                    .photoURL("/img/canon_logo.png")
                    .startingPrice(100)
                    .purchasePrice(150)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user4)
                    .build();
            itemService.saveItem(item16);

            Item item17 = Item.builder()
                    .name("GoPro Camera")
                    .description("action camera")
                    .photoURL("/img/gopro_logo.png")
                    .startingPrice(75)
                    .purchasePrice(125)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user1)
                    .build();
            itemService.saveItem(item17);

            Item item18 = Item.builder()
                    .name("Sony TV")
                    .description("TV set")
                    .photoURL("/img/sony_tv_logo.png")
                    .startingPrice(150)
                    .purchasePrice(250)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user2)
                    .build();
            itemService.saveItem(item18);

            Item item19 = Item.builder()
                    .name("LG TV")
                    .description("TV set")
                    .photoURL("/img/lg_tv_logo.png")
                    .startingPrice(10)
                    .purchasePrice(50)
                    .lastBid(0)
                    .sellable(true)
                    .seller(user3)
                    .build();
            itemService.saveItem(item19);
        }
    }
}
