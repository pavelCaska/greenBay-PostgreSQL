package com.pc.greenbay.model.response;

import com.pc.greenbay.entity.User;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private UUID id;
    private String username;
    private String password;
    private double balance;
    private String roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.balance = user.getBalance();
        this.roles = user.getRoles();
    }
}
