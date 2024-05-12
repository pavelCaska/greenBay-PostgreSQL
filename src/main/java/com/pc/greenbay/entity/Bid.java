package com.pc.greenbay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "bids")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @NotNull(message = "Bid amount is empty or missing.")
    @Min(value = 1, message = "Bid amount must be greater than or equal to 1")
    private int bidAmount;

    public Bid(Item item, User bidder, int bidAmount) {
        this.item = item;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }
}
