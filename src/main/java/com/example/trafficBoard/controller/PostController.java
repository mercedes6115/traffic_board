package com.example.trafficBoard.controller;


import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.repository.PostRepository;
import com.example.trafficBoard.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;
    private final PostRepository postRepository;

    /**
     * 게시글 조회 API
     * - 작성자 본인은 조회수 증가 X
     * - 다른 사용자는 처음 조회할 때만 조회수 +1
     */
    @GetMapping("/{postId}/view")
    public ResponseEntity<String> viewPost(@PathVariable Long postId, @RequestParam Long userId) {
        Long viewCount = postService.viewPost(postId, userId);
        Double averageRate = postService.getAverageRating(postId);


        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        String response = String.format(
                "Post Title: %s\nAuthor: %s\nCurrent View Count: %d",
                post.getTitle(),
                post.getUser_id(),
                averageRate,
                viewCount
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Object> viewPostsList(@PathVariable Long postId, @RequestParam Long userId) {
        Object response = postService.viewPostsList();

        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/rate")
    public ResponseEntity<String> ratePost(@PathVariable Long postId, @RequestParam Long userId, @RequestParam int score) {
        postService.ratePost(postId, userId, score);

       String response = String.format("Post Rate Success");

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/{postId}/like")
//    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestParam String username) {
//        return postService.likePost(postId, username);
//    }
}
