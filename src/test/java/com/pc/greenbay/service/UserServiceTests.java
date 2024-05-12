package com.pc.greenbay.service;

import com.pc.greenbay.model.response.UserDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.repository.UserRepository;
//import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup(){
        UUID userId = UUID.randomUUID();
//        String password = "u12345";
//        String encodedPassword = "encoded_" + password;
//        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        user = User.builder()
                .id(userId)
                .username("user1")
//                .password(passwordEncoder.encode(password))
                .password("u12345")
                .balance(100)
                .roles("ROLE_USER")
                .build();
    }

    @Test
    @DisplayName("JUnit test for saveUser method")
    void givenUserObject_whenSaveUser_thenSaveReturnUserObject() {
        given(userRepository.save(user)).willReturn(user);

        User savedUser = userService.saveUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("JUnit test for find user by username method")
    void givenUsername_whenFindByUsername_thenReturnUserObject() {
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("JUnit test for update user method")
    void givenUserObject_whenUpdateBalance_thenReturnUpdatedUser () throws Exception {

        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));
        user.setBalance(200);

        userService.updateBalance("user1", 200);

        assertThat(user.getBalance()).isEqualTo(200);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("JUnit test for update user method throws Exception")
    void givenUserObject_whenUpdateBalance_thenThrowException () throws Exception {

        given(userRepository.findByUsername("user2")).willReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> userService.updateBalance("user2", 200));

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    @DisplayName("JUnit test for showing GreenBay dollar balance")
    void givenUserObject_whenShowBalance_thenReturnBalance() {

        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        Double balance = userService.showGreenBayDollarsBalance("user1");

        assertThat(user.getBalance()).isEqualTo(balance);
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    @DisplayName("JUnit test for list all users with role user")
    void givenUserList_whenListAllUsers_thenReturnUserList() {

        UUID user2Id = UUID.randomUUID();
        User user2 = User.builder()
                .id(user2Id)
                .username("user2")
                .password("u23456")
                .balance(100)
                .roles("ROLE_USER")
                .build();

        given(userRepository.findAllByRolesContains("ROLE_USER")).willReturn(List.of(user, user2));

        List<UserDTO> userList = userService.listAllUsers();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(2);
        assertThat(userList.get(0).getId()).isEqualTo(user.getId());
        assertThat(userList.get(1).getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("JUnit test for password encoder")
    void givenStringPassword_whenEncodePassword_thenReturnEncodedPassword() {
        String inputPassword = "u12345";
        String encodedPassword = "encoded_u12345";

        given(passwordEncoder.encode(inputPassword)).willReturn(encodedPassword);
        String result = userService.encodePassword(inputPassword);

        verify(passwordEncoder, times(1)).encode(inputPassword);
        org.junit.jupiter.api.Assertions.assertEquals(encodedPassword, result);
    }
}
