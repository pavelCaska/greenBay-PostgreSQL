package com.pc.greenbay.model.response;

import com.pc.greenbay.entity.Item;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class ItemListDTO {
    private UUID id;
    private String name;
    private String description;
    private String photoURL;
    private int lastBid;
    private boolean sellable;
    private String sellerUsername;

    public ItemListDTO(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.photoURL = item.getPhotoURL();
        this.lastBid = item.getLastBid();
        this.sellable = item.isSellable();
        this.sellerUsername = item.getSeller().getUsername();
    }
}
