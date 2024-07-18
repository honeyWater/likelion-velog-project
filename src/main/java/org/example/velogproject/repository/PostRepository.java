package org.example.velogproject.repository;

import org.example.velogproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        select p
        from Post p
        where p.createdAt >= :oneMonthAgo
        and p.inPrivate = false
        and p.publishStatus = true
        """)
    List<Post> findMonthlyTrendingPosts(@Param("oneMonthAgo") LocalDateTime oneMonthAgo); // 월간 트렌딩 게시글 조회

    @Query("""
        select p
        from Post p
        where p.createdAt >= :twoWeeksAgo
        and p.inPrivate = false
        and p.publishStatus = true
        """)
    List<Post> findWeeklyTrendingPosts(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo); // 주간 트렌딩 게시글 조회

    @Query("""
        select p
        from Post p
        where p.createdAt >= :twoDaysAgo
        and p.inPrivate = false
        and p.publishStatus = true
        """)
    List<Post> findDailyTrendingPosts(@Param("twoDaysAgo") LocalDateTime twoDaysAgo); // 일간 트렌딩 게시글 조회

    @Query("""
        select p
        from Post p
        where p.createdAt >= :oneYearAgo
        and p.inPrivate = false
        and p.publishStatus = true
        """)
    List<Post> findYearlyTrendingPosts(@Param("oneYearAgo") LocalDateTime oneYearAgo); // 연간 트렌딩 게시글 조회

    @Query("""
        select p
        from Post p
        where p.inPrivate = false
        and p.publishStatus = true
        order by p.createdAt desc
        """)
    List<Post> findRecentPosts(); // 모든 사용자의 게시글 최신순 조회

    @Query("""
        select p
        from Post p
        where p.user.id = :userId
        and p.inPrivate = false
        and p.publishStatus = true
        order by p.createdAt desc
        """)
    List<Post> findPublishedPostsNotInPrivate(@Param("userId") Long userId); // 특정 사용자의 비공개가 아닌 출간 게시글 조회

    @Query("""
        select p
        from Post p
        where p.user.id = :userId
        and p.publishStatus = true
        order by p.createdAt desc
        """)
    List<Post> findPublishedPostsAlsoInPrivate(@Param("userId") Long userId); // 특정 사용자의 비공개가 포함된 출간 게시글 조회

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Post> findByUserIdAndPublishStatusFalse(Long userId);

    @Query("""
        SELECT p
        FROM Post p
        JOIN p.tags t
        WHERE p.user.id = :userId
        AND p.publishStatus = true
        AND p.inPrivate = false
        AND t.tagName = :tagName
        """)
    List<Post> findPublishedPostsNotInPrivateByTag(@Param("tagName") String tagName, @Param("userId") Long userId);

    @Query("""
        SELECT p
        FROM Post p
        JOIN p.tags t
        WHERE p.user.id = :userId
        AND p.publishStatus = true
        AND t.tagName = :tagName
        """)
    List<Post> findPublishedPostsAlsoInPrivateByTag(@Param("tagName") String tagName, @Param("userId") Long userId);
}
