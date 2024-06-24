package org.example.velogproject.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.dto.PostCardDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("월간 트렌딩 게시글 조회")
    void getMonthlyTrendingPosts() {
        List<PostCardDto> posts = postService.getMonthlyTrendingPosts();
        for(PostCardDto post : posts){
            log.info(post.toString());
        }
    }

    @Test
    @DisplayName("주간 트렌딩 게시글 조회")
    void getWeeklyTrendingPosts() {
        List<PostCardDto> posts = postService.getWeeklyTrendingPosts();
        for(PostCardDto post : posts){
            log.info(post.toString());
        }
    }
}