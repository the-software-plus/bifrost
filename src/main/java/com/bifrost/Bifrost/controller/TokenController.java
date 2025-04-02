package com.bifrost.Bifrost.controller;

import com.bifrost.Bifrost.controller.dto.LoginRequest;
import com.bifrost.Bifrost.controller.dto.LoginResponse;
import com.bifrost.Bifrost.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest requestBody) {
        var user = userRepository.findByEmail(requestBody.email());

        if (user.isEmpty() || !user.get().isPasswordCorrect(requestBody, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Credential not valid");
        }

        Instant now = Instant.now();
        Long expiresIn = 300L;

        var claims = JwtClaimsSet.builder()
                .issuer("bifrost")
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }
}
