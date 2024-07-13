package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.Post;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.repository.PostRepository;
import org.example.velogproject.util.SlugUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
                .slug(post.getSlug())
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

    // post id 로 게시글 조회
    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // 슬러그로 게시글 조회
    @Transactional(readOnly = true)
    public Optional<Post> findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    // 게시글 생성
    @Transactional
    public Post createPost(Post post) {
        // title 을 독립적인 슬러그 값으로 변환 후 저장
        String uniqueSlug = SlugUtils.generateUniqueSlug(post.getTitle(), postRepository);
        post.setSlug(uniqueSlug);
        return postRepository.save(post);
    }

    // 첫 게시글 임시저장
    @Transactional
    public Post savePostFirstTemporarily(Post post) {
        // 첫 임시저장 세팅
        post.setDescription(setDescriptionByContent(post.getContent()));
        post.setCreatedAt(LocalDateTime.now());
        post.setPublishStatus(false);   // 출간 x
        post.setInPrivate(false);       // 비공개 x
        post.setViewCount(0L);
        post.setLikeCount(0);
        post.setCommentCount(0);

        // title 을 독립적인 슬러그 값으로 변환 후 저장
        String uniqueSlug = SlugUtils.generateUniqueSlug(post.getTitle(), postRepository);
        post.setSlug(uniqueSlug);

        return postRepository.save(post);
    }

    // 기존 게시글 존재 시 해당 게시글을 업데이트
    @Transactional
    public Post updatePostTemporarily(Post post) {
        Optional<Post> existedPost = getPostById(post.getId());
        if (existedPost.isPresent()) {
            Post postToUpdate = existedPost.get();

            // 필요한 업데이트 작업 수행
            postToUpdate.setTitle(post.getTitle());
            postToUpdate.setContent(post.getContent());
            if (!post.isPublishStatus()) {
                // 출간한 적이 없다면, description 설정
                postToUpdate.setDescription(setDescriptionByContent(post.getContent()));
            }

            // 추가로 필요한 필드 업데이트 (slug)
            postToUpdate.setSlug(SlugUtils.generateUniqueSlug(post.getTitle(), postRepository));

            return postRepository.save(postToUpdate);
        } else {
            // 기존 게시글이 없을 경우 예외 처리
            throw new RuntimeException("게시글을 찾을 수 없습니다: " + post.getId());
        }
    }

    // content 에서 HTML 문법 및 마크다운을 제거하고 150 자로 자른 문자열을 반환
    private String setDescriptionByContent(String content) {
        String plainTextContent = Jsoup.parse(content).text();
        if (plainTextContent.length() > 147) {
            return plainTextContent.substring(0, 147) + "...";
        } else {
            return plainTextContent;
        }
    }
}
