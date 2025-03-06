package com.example.trafficBoard.repository;

import com.example.trafficBoard.entity.LikesEntity;
import com.example.trafficBoard.entity.PostsEntity;
import com.example.trafficBoard.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesEntityRepository extends JpaRepository<LikesEntity, Long> {

    Optional<LikesEntity> findByUserAndPost(UserEntity user, PostsEntity post);
    int countByPost(PostsEntity post);

    @Transactional
    void deleteByUserAndPost(UserEntity user, PostsEntity post);
}
