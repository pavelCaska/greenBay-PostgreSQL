package com.pc.greenbay.model.response;

import com.pc.greenbay.entity.Item;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ItemPageDTO {
    private UUID id;
    private String name;
    private String photoURL;
    private int lastBid;
    private String sellerUsername;

    public ItemPageDTO(Item item) {
      this.id = item.getId();
      this.name = item.getName();
      this.photoURL = item.getPhotoURL();
      this.lastBid = item.getLastBid();
      this.sellerUsername = item.getSeller().getUsername();
    }
}
