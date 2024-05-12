package com.pc.greenbay.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pc.greenbay.entity.Item;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ItemResponseDTO {

    @JsonProperty("item_ID")
    private String itemID;
    private String name;
    private String description;
    private String photoURL;
    @JsonProperty("starting_price")
    private Integer startingPrice;
    @JsonProperty("purchase_price")
    private Integer purchasePrice;

    public static ItemResponseDTO fromEntity(Item item) {
        if (item == null) {
            return null;
        }

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setItemID(item.getId().toString());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPhotoURL(item.getPhotoURL());
        dto.setStartingPrice(item.getStartingPrice());
        dto.setPurchasePrice(item.getPurchasePrice());

        return dto;

    }
}
