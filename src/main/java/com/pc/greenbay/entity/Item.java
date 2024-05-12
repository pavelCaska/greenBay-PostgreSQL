package com.pc.greenbay.entity;

import com.pc.greenbay.model.request.ItemRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "items")
public class Item {
//    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "uuid2",
//            parameters = {
//                    @org.hibernate.annotations.Parameter(
//                            name = "uuid_gen_strategy_class",
//                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
//                    )
//            }
//    )
//    @Column(name = "id", nullable = false, updatable = false)
//    private UUID id;

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Item name is empty or missing.")
    private String name;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Item description is empty or missing.")
    private String description;

    @NotBlank(message = "URL is empty or missing.")
    @Pattern(regexp = "^\\/img\\/[a-zA-Z0-9_-]+\\.(?:jpg|gif|png)$", message = "Invalid path")
//    @URL(message = "Please provide a valid URL")
    private String photoURL;

    @NotNull(message = "Starting price is empty or missing.")
    @Min(value = 1, message = "Starting price must be greater than or equal to 1" )
    private int startingPrice;

    @NotNull(message = "Purchase price is empty or missing.")
    @Min(value = 1, message = "Purchase price must be greater than or equal to 1")
    private int purchasePrice;

    @Builder.Default
    private int lastBid = 0;

    @Builder.Default
    private boolean sellable = true;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchases = new ArrayList<>();

    public Item(ItemRequestDTO itemRequestDTO, User seller) {
        this.name = itemRequestDTO.getName();
        this.description = itemRequestDTO.getDescription();
        this.photoURL = itemRequestDTO.getPhotoURL();
        this.startingPrice = itemRequestDTO.getStartingPrice();
        this.purchasePrice = itemRequestDTO.getPurchasePrice();
        this.seller = seller;
        this.sellable = true;
        this.lastBid = 0;
        this.bids = new ArrayList<>();
        this.purchases = new ArrayList<>();
    }
}
