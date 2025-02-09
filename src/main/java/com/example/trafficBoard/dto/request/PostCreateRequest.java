package com.example.trafficBoard.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

    private Long userId;  // 작성자 ID
    private String title;  // 게시글 제목
    private String content;  // 게시글 내용
}
