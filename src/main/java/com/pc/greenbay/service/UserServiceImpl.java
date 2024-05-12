package com.pc.greenbay.service;

import com.pc.greenbay.model.MessageDTO;
import com.pc.greenbay.model.response.UserDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public String encodePassword(String pwd) {
        return passwordEncoder.encode(pwd);
    }

    @Override
    public Map<String, String> buildErrorResponseForLogin(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
    }

    @Override
    public double showGreenBayDollarsBalance(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return user.getBalance();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Override
    public MessageDTO updateBalance(String username, double balance) {
        User user = this.findByUsername(username);
        user.setBalance(balance);
        userRepository.save(user);

        return new MessageDTO("Balance successfully updated to " + balance);
    }

    @Override
    public List<UserDTO> listAllUsers() {
        return userRepository.findAllByRolesContains("ROLE_USER").stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
