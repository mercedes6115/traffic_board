package com.example.trafficBoard.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@Table(name = "posts")
public class PostsEntity extends GeneralEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    @Id
    private Long post_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "views")
    private Integer views;

    @Column(name = "average_rating")
    private BigDecimal average_rating;

}
