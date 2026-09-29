package com.mams.controller;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mams.repository.UserRepository;
import com.mams.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    UserRepository users;
    PasswordEncoder enc;
    JwtService jwt;

    public AuthController(UserRepository u, PasswordEncoder e, JwtService j) {
        users = u;
        enc = e;
        jwt = j;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {

        var u = users.findByUsername(body.get("username"))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!enc.matches(body.get("password"), u.password))
            throw new RuntimeException("Invalid credentials");

        return Map.of(
                "token", jwt.generate(u.username, u.role.name()),
                "username", u.username,
                "role", u.role.name(),
                "baseId", u.base == null ? 0 : u.base.id
        );
    }
}