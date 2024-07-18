package org.example.velogproject.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.PostCardDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("월간 트렌딩 게시글 조회")
    void getMonthlyTrendingPosts() {
        List<PostCardDto> posts = postService.getMonthlyTrendingPosts();
        for (PostCardDto post : posts) {
            log.info(post.toString());
        }
    }

    @Test
    @DisplayName("주간 트렌딩 게시글 조회")
    void getWeeklyTrendingPosts() {
        List<PostCardDto> posts = postService.getWeeklyTrendingPosts();
        for (PostCardDto post : posts) {
            log.info(post.toString());
        }
    }

    @Test
    @Commit
    @DisplayName("게시글 테스트 데이터 생성")
    void createPost() {
        for (int j = 0; j < 3; j++) {
            Optional<User> user = userService.getUserById((long) (j + 2));

            for (int i = 0; i < 5; i++) {
                Post post = new Post();
                user.ifPresent(post::setUser);
                post.setTitle("테스트 게시글 " + (i + 1));
                post.setDescription("테스트 게시글입니다.");
                post.setContent("테스트 게시글 " + (i + 1) + "입니다.");
                post.setCreatedAt(LocalDateTime.of(2020 + i, i + 3, 10, 18, 0, 0));
                post.setInPrivate(i != 4);
                post.setPublishStatus(i != 3);
                post.setViewCount((long) ((i + 1) * 100));
                post.setLikeCount(0);
                post.setCommentCount(0);

                Post createdPost = postService.createPost(post);
                log.info(createdPost.toString());
            }
        }
    }

    @Test
    @DisplayName("태그로 필터링하여 출간된 게시글 조회")
    void getPublishedPostsNotInPrivateByTag() {
        String tagName = "태그1";
        Long userId = 4L;
        List<PostCardDto> posts = postService.getPublishedPostsNotInPrivateByTag(tagName, userId);
        log.info("Found {} posts for tag '{}' and user ID {}", posts.size(), tagName, userId);
    }

    @Test
    void getPublishedPostsAlsoInPrivateByTag() {
    }
}