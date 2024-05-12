package com.pc.greenbay.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequestDTO {
    @NotBlank(message = "Username is empty or missing.")
    private String username;

//    @NotBlank(message = "Password is empty or missing.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$", message = "Password must consists of a minimum of 6 letters and/or digits.")
    private String password;

}
