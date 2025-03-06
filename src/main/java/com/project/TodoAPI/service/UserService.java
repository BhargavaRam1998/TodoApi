package com.project.TodoAPI.service;

import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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

       String token = jwtUtil.generateToken(user.getEmail());

        response.put("token", token);
        return response;

    }
}
