package com.pc.greenbay.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BidRequestDTO {

    @NotNull(message = "Bid amount is empty or missing.")
    @Min(value = 1, message = "Bid amount must be greater than or equal to 1")
    private int bidAmount;

}
