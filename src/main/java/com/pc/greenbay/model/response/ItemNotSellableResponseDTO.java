package com.pc.greenbay.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pc.greenbay.entity.Item;
import com.pc.greenbay.entity.Purchase;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor

public class ItemNotSellableResponseDTO extends ItemCommonResponseDTO {
    @JsonProperty("buyer")
    private String buyerUsername;
    @JsonProperty("buying_price")
    private int buyingPrice;

    public ItemNotSellableResponseDTO(String name, String description, String photoURL, String sellerUsername, String buyerUsername, int buyingPrice) {
        super(name, description, photoURL, sellerUsername);
        this.buyerUsername = buyerUsername;
        this.buyingPrice = buyingPrice;
    }

    public ItemNotSellableResponseDTO(Item item, Purchase purchase) {
        super(item.getName(), item.getDescription(), item.getPhotoURL(), item.getSeller().getUsername());
        this.buyerUsername = purchase.getBuyer().getUsername();
        this.buyingPrice = purchase.getPurchaseAmount();
    }
}
