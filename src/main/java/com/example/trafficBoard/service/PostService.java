package com.example.trafficBoard.service;

import com.example.trafficBoard.dto.request.PostCreateRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.beans.Transient;

public interface PostService {


    @Transactional
    public ResponseEntity<Object> createPost(PostCreateRequest request);


}
