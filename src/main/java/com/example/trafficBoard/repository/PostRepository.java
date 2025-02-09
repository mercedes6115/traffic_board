package com.example.trafficBoard.repository;

import com.example.trafficBoard.entity.PostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostsEntity,Long> {


}
