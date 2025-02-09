package com.example.trafficBoard.controller;


import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.repository.PostRepository;
import com.example.trafficBoard.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        String response = String.format(
                "Post Title: %s\nAuthor: %s\nCurrent View Count: %d",
                post.getTitle(),
                post.getUser_id(),
                viewCount
        );

        return ResponseEntity.ok(response);
    }
}
