package org.example.velogproject.repository;

import org.example.velogproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
            select p
            from Post p
            where p.createdAt >= :oneMonthAgo
            and p.inPrivate = false
            and p.publishStatus = true
            """)
    List<Post> findMonthlyTrendingPosts(@Param("oneMonthAgo") LocalDateTime oneMonthAgo);

    @Query("""
            select p
            from Post p
            where p.createdAt >= :twoWeeksAgo
            and p.inPrivate = false
            and p.publishStatus = true
            """)
    List<Post> findWeeklyTrendingPosts(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo);

    @Query("""
            select p
            from Post p
            where p.createdAt >= :twoDaysAgo
            and p.inPrivate = false
            and p.publishStatus = true
            """)
    List<Post> findDailyTrendingPosts(@Param("twoDaysAgo") LocalDateTime twoDaysAgo);

    @Query("""
            select p
            from Post p
            where p.createdAt >= :oneYearAgo
            and p.inPrivate = false
            and p.publishStatus = true
            """)
    List<Post> findYearlyTrendingPosts(@Param("oneYearAgo") LocalDateTime oneYearAgo);

    @Query("""
            select p
            from Post p
            where p.inPrivate = false
            and p.publishStatus = true
            order by p.createdAt desc
            """)
    List<Post> findRecentPosts();
}
