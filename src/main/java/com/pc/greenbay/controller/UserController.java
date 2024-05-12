package com.pc.greenbay.controller;

import com.pc.greenbay.model.ErrorDTO;
import com.pc.greenbay.model.request.LoginRequestDTO;
import com.pc.greenbay.service.JwtService;
import com.pc.greenbay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Autowired
    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userService.buildErrorResponseForLogin(bindingResult));
        }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()));
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("access_token", jwtService.generateToken(loginRequestDTO.getUsername()));
            response.put("balance", userService.showGreenBayDollarsBalance(loginRequestDTO.getUsername()));

            return ResponseEntity.ok(response);
    }

    @GetMapping("/temp/isRunning")
    public String isRunning() {
        return "Service is running";
    }

    @GetMapping("/user")
    public ResponseEntity<?> showUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @PatchMapping("/temp/balance/{username}")
    public ResponseEntity<?> updateBalance(@PathVariable String username, @RequestParam(name = "newBalance", required = false) Double newBalance) {
        if(newBalance == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Parameter 'newBalance' is missing."));
        }
        if(newBalance < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("New balance must be a positive number."));
        }
            return ResponseEntity.ok().body(userService.updateBalance(username, newBalance));
    }
}
