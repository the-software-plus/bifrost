package com.bifrost.Bifrost.controller;

import com.bifrost.Bifrost.controller.dto.RegisterRequest;
import com.bifrost.Bifrost.entities.Role;
import com.bifrost.Bifrost.entities.User;
import com.bifrost.Bifrost.repository.RoleRepository;
import com.bifrost.Bifrost.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class RegisterController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest requestBody) {
        var user = userRepository.findByEmail(requestBody.email());

        if (user.isPresent()) {
            throw new RuntimeException("Email already register");
        }

        User newUser = new User();
        Role role = roleRepository.findByName(Role.Values.BASIC.name());

        newUser.setEmail(requestBody.email());
        newUser.setUsername(requestBody.username());
        newUser.setPassword(bCryptPasswordEncoder.encode(requestBody.password()));
        newUser.setRoles(Set.of(role));

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
