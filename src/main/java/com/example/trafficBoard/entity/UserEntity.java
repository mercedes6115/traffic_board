package com.example.trafficBoard.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "user")
public class UserEntity extends GeneralEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Id
    private Long user_id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;


}
