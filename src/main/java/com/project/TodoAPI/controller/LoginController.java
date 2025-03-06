package com.project.TodoAPI.controller;

import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user){
        Optional<Users> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isPresent() && passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(existingUser.get().getEmail());

            HashMap<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);
            //return ResponseEntity.ok("{\"token\":\"" + token + "\"}"); --we can either follow the above hashmap approach or this one.
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid Credentials\"}");
    }
}
