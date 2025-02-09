package com.example.trafficBoard.service.impl;

import com.example.trafficBoard.dto.request.PostCreateRequest;
import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.repository.PostRepository;
import com.example.trafficBoard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final StringRedisTemplate redisTemplate;
    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewCount:";
    private static final String LIKE_KEY_PREFIX = "post:likes:";
    private static final String RATING_KEY_PREFIX = "post:ratings:";
    private static final String VIEWED_USERS_KEY_PREFIX = "post:viewedUsers:";

    private final PostRepository postRepository;

    public Long incrementViewCount(Long postId) {
        String redisKey = VIEW_COUNT_KEY_PREFIX + postId;
        return redisTemplate.opsForValue().increment(redisKey);
    }

    // 현재 조회수 가져오기
    public Long getViewCount(Long postId) {
        String redisKey = VIEW_COUNT_KEY_PREFIX + postId;
        String count = redisTemplate.opsForValue().get(redisKey);
        return count != null ? Long.valueOf(count) : 0L;
    }

    public boolean toggleLike(Long postId, Long userId) {
        String redisKey = LIKE_KEY_PREFIX + postId;
        Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId.toString());

        if (Boolean.TRUE.equals(isLiked)) {
            redisTemplate.opsForSet().remove(redisKey, userId.toString());
            return false;  // 좋아요 취소
        } else {
            redisTemplate.opsForSet().add(redisKey, userId.toString());
            return true;  // 좋아요 추가
        }
    }

    // 좋아요 개수 조회
    public Long getLikeCount(Long postId) {
        String redisKey = LIKE_KEY_PREFIX + postId;
        return redisTemplate.opsForSet().size(redisKey);
    }

    // 특정 사용자가 좋아요 눌렀는지 확인
    public boolean isLikedByUser(Long postId, Long userId) {
        String redisKey = LIKE_KEY_PREFIX + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(redisKey, userId.toString()));
    }

    public void ratePost(Long postId, Long userId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        String redisKey = RATING_KEY_PREFIX + postId;
        redisTemplate.opsForHash().put(redisKey, userId.toString(), String.valueOf(rating));
    }

    // 평균 평점 조회
    public double getAverageRating(Long postId) {
        String redisKey = RATING_KEY_PREFIX + postId;
        Map<Object, Object> ratings = redisTemplate.opsForHash().entries(redisKey);

        if (ratings.isEmpty()) return 0.0;

        double sum = ratings.values().stream()
                .mapToDouble(r -> Double.parseDouble(r.toString()))
                .sum();

        return sum / ratings.size();
    }

    @Override
    public ResponseEntity<Object> createPost(PostCreateRequest request) {


        PostsEntity entity = new PostsEntity();
        entity.setPost_id(request.getUserId());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());

        postRepository.save(entity);

        return null;
    }

    /**
     * 게시글 조회 시, 작성자가 아니고 중복 조회가 아닐 때만 조회수 증가
     */
    public Long viewPost(Long postId, Long userId) {
        // 게시글 가져오기
        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 작성자 본인인지 확인
        if (!post.getUser_id().equals(userId)) {
            String viewedUsersKey = VIEWED_USERS_KEY_PREFIX + postId;
            String viewCountKey = VIEW_COUNT_KEY_PREFIX + postId;

            // 이 사용자가 이미 조회했는지 확인 (중복 방지)
            Boolean hasViewed = redisTemplate.opsForSet().isMember(viewedUsersKey, userId.toString());

            if (Boolean.FALSE.equals(hasViewed)) {
                // 처음 조회한 경우에만 조회수 증가
                redisTemplate.opsForValue().increment(viewCountKey);
                // 사용자 ID를 조회자 목록에 추가
                redisTemplate.opsForSet().add(viewedUsersKey, userId.toString());
            }
        }

        // 최종 조회수 반환
        String currentViewCount = redisTemplate.opsForValue().get(VIEW_COUNT_KEY_PREFIX + postId);
        return currentViewCount != null ? Long.parseLong(currentViewCount) : post.getViews();
    }
}
