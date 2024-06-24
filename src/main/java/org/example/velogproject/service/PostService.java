package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Post;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 게시글 Post -> PostCardDto
    public List<PostCardDto> toDto(List<Post> posts) {
        return posts.stream()
                .map(post -> PostCardDto.builder()
                        .id(post.getId())
                        .user(post.getUser())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .createdAt(post.getCreatedAt())
                        .thumbnailImage(post.getThumbnailImage())
                        .inPrivate(post.isInPrivate())
                        .publishStatus(post.isPublishStatus())
                        .viewCount(post.getViewCount())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .build())
                .collect(Collectors.toList());
    }

    // 게시글 트렌딩 처리
    public List<PostCardDto> doTrending(List<Post> posts) {
        return toDto(posts.stream()
                .sorted((post1, post2) -> Double.compare(
                        post2.getViewCount() * 0.075 + post2.getLikeCount(),
                        post1.getViewCount() * 0.075 + post1.getLikeCount()))
                .collect(Collectors.toList()));
    }

    // 월간 트렌딩 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getMonthlyTrendingPosts() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Post> posts = postRepository.findMonthlyTrendingPosts(oneMonthAgo);
        return doTrending(posts);
    }

    // 주간 트렌딩 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getWeeklyTrendingPosts() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Post> posts = postRepository.findWeeklyTrendingPosts(twoWeeksAgo);
        return doTrending(posts);
    }

    // 일간 트렌딩 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getDailyTrendingPosts() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Post> posts = postRepository.findDailyTrendingPosts(oneDayAgo);
        return doTrending(posts);
    }

    // 연간 트렌딩 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getYearlyTrendingPosts() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Post> posts = postRepository.findYearlyTrendingPosts(oneYearAgo);
        return doTrending(posts);
    }

    // 최신순 게시글 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getRecentPosts() {
        return toDto(postRepository.findRecentPosts());
    }
}
