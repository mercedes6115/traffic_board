package com.example.trafficBoard.controller;


import com.example.trafficBoard.dto.request.PostCreateRequest;
import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.repository.PostRepository;
import com.example.trafficBoard.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
                post.getUser().getUser_id(),
                averageRate,
                viewCount
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/post/view/list")
    public ResponseEntity<Object> viewPostsList() {
        Object response = postService.viewPostsList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/rate")
    public ResponseEntity<String> ratePost(@PathVariable Long postId, @RequestParam Long userId, @RequestParam int score) {
        postService.ratePost(postId, userId, score);

        String response = String.format("Post Rate Success");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/post")
    public ResponseEntity<String> createPost(@RequestBody PostCreateRequest request) {
        postService.createPost(request);

        String response = String.format("Post Rate Success");
        return ResponseEntity.ok(response);
    }


    // 게시글 좋아요 추가/삭제
    @PostMapping("/{postId}/toggle/{userId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, @PathVariable Long userId) {
        boolean liked = postService.toggleLike(postId, userId);
        return ResponseEntity.ok(liked ? "Liked" : "Unliked");
    }

    // 게시글의 좋아요 수 조회
    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Long likesCount = postService.getLikeCount(postId);
        return ResponseEntity.ok(likesCount);
    }
}
