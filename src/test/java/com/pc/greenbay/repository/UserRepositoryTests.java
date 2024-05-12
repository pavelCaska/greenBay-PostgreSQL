package com.pc.greenbay.repository;

import com.pc.greenbay.entity.User;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("u12345"))
                .balance(100)
                .roles("ROLE_USER")
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("JUnit test for save user operation")
    void givenUserObject_whenSave_thenReturnSavedUser() {
        User toBeSavedUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("u23456"))
                .balance(300)
                .roles("ROLE_USER")
                .build();

        User savedUser = userRepository.save(toBeSavedUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("user");
        assertThat(passwordEncoder.matches("u23456", savedUser.getPassword())).isTrue();
        assertThat(savedUser.getRoles()).isEqualTo("ROLE_USER");

    }
    @Test
    @DisplayName("JUnit test for update user operation")
    void givenUserObject_whenUpdateUser_thenReturnUpdatedUser() {

        User foundUser = userRepository.findByUsername(user.getUsername()).get();
        foundUser.setBalance(201);
        User updatedUser = userRepository.save(foundUser);

        assertThat(updatedUser.getBalance()).isEqualTo(201);
    }

    @Test
    @DisplayName("JUnit test for find user by username operation")
    void givenUserObject_whenFindByUsername_thenReturnUserObject() {

        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(optionalUser.get().getBalance()).isEqualTo(user.getBalance());
        assertThat(optionalUser.get().getRoles()).isEqualTo(user.getRoles());
        assertThat(optionalUser.get().getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("JUnit test for find all users by roles operation")
    void givenUserObjects_whenFindAllByRolesContains_thenReturnList() {
        User admin = User.builder()
                .username("admin")
                .password("A12345")
                .balance(100)
                .roles("ROLE_ADMIN")
                .build();
        userRepository.save(admin);

        List<User> userList = userRepository.findAllByRolesContains("ROLE_USER");

        assertThat(userList).isNotNull();
        assertThat(userList).size().isEqualTo(1);
        assertThat(userList).contains(user);
    }
}
