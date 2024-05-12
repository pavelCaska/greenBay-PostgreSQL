package com.pc.greenbay.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
//@AllArgsConstructor

public class BidPlacedResponseDTO extends BidCommonResponseDTO {
    @JsonProperty("bid_placed")
    private int bidAmount;

    public BidPlacedResponseDTO(String name, String description, String photoURL, String sellerUsername, int bidAmount) {
        super(name, description, photoURL, sellerUsername);
        this.bidAmount = bidAmount;
    }
}
