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
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @NotNull(message = "Purchase amount is empty or missing.")
    @Min(value = 1, message = "Purchase amount must be greater than or equal to 1")
    private int purchaseAmount;

    public Purchase(Item item, User buyer, int purchaseAmount) {
        this.item = item;
        this.buyer = buyer;
        this.purchaseAmount = purchaseAmount;
    }
}
