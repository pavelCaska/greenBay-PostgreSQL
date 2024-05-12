package com.pc.greenbay.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BidCommonResponseDTO {
    private String name;
    private String description;
    private String photoURL;
    @JsonProperty("seller")
    private String sellerUsername;

}
