package com.example.trafficBoard.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "post_rating")
public class PostRatingEntity extends GeneralEntity{


    @Id
    @Column(name = "postrating_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postrating_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostsEntity post;


    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer rating;  // 평점 (1~5)

}
