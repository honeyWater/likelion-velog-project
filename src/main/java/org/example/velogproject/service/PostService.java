package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Post;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.repository.PostRepository;
import org.example.velogproject.util.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
                .description(post.getDescription())
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
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Post> posts = postRepository.findDailyTrendingPosts(twoDaysAgo);
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

    // 특정 사용자의 비공개, 임시 출간이 아닌 게시물 전체 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getPublishedPostsNotInPrivate(Long userId) {
        List<Post> posts = postRepository.findPublishedPostsNotInPrivate(userId);
        return toDto(posts);
    }

    // 특정 사용자의 임시 출간이 아니고, 비공개인 게시물 조회
    @Transactional(readOnly = true)
    public List<PostCardDto> getPublishedPostsAlsoInPrivate(Long userId) {
        List<Post> posts = postRepository.findPublishedPostsAlsoInPrivate(userId);
        return toDto(posts);
    }

    // 슬러그로 게시글 조회
    @Transactional(readOnly = true)
    public Optional<Post> findBySlug(String slug){
        return postRepository.findBySlug(slug);
    }

    // 게시글 생성
    @Transactional
    public Post createPost(Post post){
        // title 을 독립적인 슬러그 값으로 변환 후 저장
        String uniqueSlug = SlugUtils.generateUniqueSlug(post.getTitle(), postRepository);
        post.setSlug(uniqueSlug);
        return postRepository.save(post);
    }
}
