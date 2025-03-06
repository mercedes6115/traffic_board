package com.example.trafficBoard.controller;


import com.example.trafficBoard.dto.request.PostCreateRequest;
import com.example.trafficBoard.dto.request.UserCreateRequest;
import com.example.trafficBoard.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class UserController {


    private final UserServiceImpl userService;
    @PostMapping("/create/user")
    public ResponseEntity<String> createPost(@RequestBody UserCreateRequest request) {
        userService.createUser(request);

        String response = String.format("User Create Success");

        return ResponseEntity.ok(response);
    }
}
