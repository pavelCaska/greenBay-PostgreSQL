package com.pc.greenbay.service;

import com.pc.greenbay.model.MessageDTO;
import com.pc.greenbay.model.response.UserDTO;
import com.pc.greenbay.entity.User;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public interface UserService {
    User saveUser(User user);

    String encodePassword(String pwd);

    Map<String, String> buildErrorResponseForLogin(BindingResult bindingResult);

    double showGreenBayDollarsBalance(String username);

    User findByUsername(String username);

    MessageDTO updateBalance(String username, double balance);

    List<UserDTO> listAllUsers();
}
