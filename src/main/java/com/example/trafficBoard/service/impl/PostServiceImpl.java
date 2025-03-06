package com.example.trafficBoard.service.impl;

import com.example.trafficBoard.dto.request.PostCreateRequest;
import com.example.trafficBoard.entity.LikesEntity;
import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.entity.UserEntity;
import com.example.trafficBoard.repository.LikesEntityRepository;
import com.example.trafficBoard.repository.PostRepository;
import com.example.trafficBoard.repository.UserRepository;
import com.example.trafficBoard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final StringRedisTemplate redisTemplate;
    private final LikesEntityRepository likesRepository;
    private final UserRepository userRepository;
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
        String key = LIKE_KEY_PREFIX + postId;  // Redis 키 생성

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Redis에서 좋아요 여부 확인
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, String.valueOf(userId));

        if (Boolean.TRUE.equals(isLiked)) {
            // Redis에서 삭제 (좋아요 취소)
            redisTemplate.opsForSet().remove(key, String.valueOf(userId));
            likesRepository.deleteByUserAndPost(user, post);
            return false;
        } else {
            // Redis에 추가 (좋아요 등록)
            redisTemplate.opsForSet().add(key, String.valueOf(userId));
            LikesEntity newLike = new LikesEntity();
            newLike.setUser(user);
            newLike.setPost(post);
            likesRepository.save(newLike);
            return true;
        }
    }

    // 좋아요 개수 조회
    public Long getLikeCount(Long postId) {
        String key = LIKE_KEY_PREFIX + postId;

        // Redis에서 좋아요 수 조회
        Long count = redisTemplate.opsForSet().size(key);

        // Redis에 없으면 DB에서 가져와서 저장
        if (count == null) {
            count = (long) likesRepository.countByPost(postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found")));
            redisTemplate.opsForSet().add(key, String.valueOf(count));  // Redis에 저장
        }

        return (long) count.intValue();

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

    public Object viewPostsList(){

        return postRepository.findAll();
    }

    /**
     * 게시글 조회 시, 작성자가 아니고 중복 조회가 아닐 때만 조회수 증가
     */
    public Long viewPost(Long postId, Long userId) {
        PostsEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getUser_id().equals(userId)) {
            String viewedUsersKey = VIEWED_USERS_KEY_PREFIX + postId;
            String viewCountKey = VIEW_COUNT_KEY_PREFIX + postId;

            Boolean hasViewed = redisTemplate.opsForSet().isMember(viewedUsersKey, userId.toString());

            if (Boolean.FALSE.equals(hasViewed)) {
                Long currentViewCount = redisTemplate.opsForValue().increment(viewCountKey);
                redisTemplate.opsForSet().add(viewedUsersKey, userId.toString());

                // 조회수가 100회 초과하면 DB 반영
                if (currentViewCount != null && currentViewCount % 100 == 0) {
                    post.setViews(currentViewCount.intValue());
                    postRepository.save(post);
                }
            }
        }

        String finalViewCount = redisTemplate.opsForValue().get(VIEW_COUNT_KEY_PREFIX + postId);
        return finalViewCount != null ? Long.parseLong(finalViewCount) : post.getViews();
    }


}
