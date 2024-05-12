package com.pc.greenbay.model.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BidListDTO {

    private Long id;
    private String bidderUsername;
    private int bidAmount;

}
