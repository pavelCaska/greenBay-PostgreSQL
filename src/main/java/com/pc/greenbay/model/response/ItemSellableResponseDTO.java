package com.pc.greenbay.model.response;

import com.pc.greenbay.entity.Item;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
//@AllArgsConstructor

public class ItemSellableResponseDTO extends ItemCommonResponseDTO {
    private List<BidListDTO> bidList;

    public ItemSellableResponseDTO(String name, String description, String photoURL, String sellerUsername, List<BidListDTO> bidList) {
        super(name, description, photoURL, sellerUsername);
        this.bidList = bidList;
    }

    public ItemSellableResponseDTO(Item item, List<BidListDTO> bidList) {
        super(item.getName(), item.getDescription(), item.getPhotoURL(), item.getSeller().getUsername());
        this.bidList = bidList;
    }

}
