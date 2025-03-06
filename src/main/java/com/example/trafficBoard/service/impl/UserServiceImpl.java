package com.example.trafficBoard.service.impl;

import com.example.trafficBoard.dto.request.UserCreateRequest;
import com.example.trafficBoard.entity.UserEntity;
import com.example.trafficBoard.repository.UserRepository;
import com.example.trafficBoard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    public String createUser(UserCreateRequest request) {

        UserEntity user = new UserEntity();

        user.setUsername(request.getName());
        user.setPassword(request.getPwd());

        userRepository.save(user);

        return "success";
    }
}
