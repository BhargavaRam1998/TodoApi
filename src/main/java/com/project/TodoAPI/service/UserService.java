package com.project.TodoAPI.service;

import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.repo.UserRepo;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    ;
    public HashMap<String, String> registerUser(Users user) {
        HashMap<String, String> response = new HashMap<>();
       Optional<Users> existingUser =  userRepo.findByEmail(user.getEmail());

       if (existingUser.isPresent()) {
           response.put("Error!","Email is already in use, please enter a new one");
           return response;
       }

       //Encoding the password entered by user before saving it to database
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       userRepo.save(user);

        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        response.put("token", token);
        return response;

    }
}
