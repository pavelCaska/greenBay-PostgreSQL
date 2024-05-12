package com.pc.greenbay.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
//@AllArgsConstructor

public class ItemBoughtResponseDTO extends BidCommonResponseDTO {
    @JsonProperty("buyer")
    private String buyerUsername;
    @JsonProperty("bought_at")
    private int buyingPrice;

    public ItemBoughtResponseDTO(String name, String description, String photoURL, String sellerUsername, String buyerUsername, int buyingPrice) {
        super(name, description, photoURL, sellerUsername);
        this.buyerUsername = buyerUsername;
        this.buyingPrice = buyingPrice;
    }
}
