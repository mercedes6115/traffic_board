package com.example.trafficBoard.service;

import com.example.trafficBoard.dto.request.PostCreateRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.beans.Transient;

public interface PostService {


    @Transactional
    public ResponseEntity<Object> createPost(PostCreateRequest request);

    @Transactional
    public boolean toggleLike(Long postId, Long userId) ;

    @Transactional
    public Long getViewCount(Long postId);

    @Transactional
    public Long incrementViewCount(Long postId);

    @Transactional
    public Long getLikeCount(Long postId);

    @Transactional
    public boolean isLikedByUser(Long postId, Long userId);

    @Transactional
    public void ratePost(Long postId, Long userId, int rating);

    @Transactional
    public double getAverageRating(Long postId);

    @Transactional
    public Object viewPostsList();

    @Transactional
    public Long viewPost(Long postId, Long userId);
    }
