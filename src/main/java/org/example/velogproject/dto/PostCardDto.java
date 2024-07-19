package org.example.velogproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.Comment;
import org.example.velogproject.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostCardDto {
    private Long id;
    private User user;
    private String title;
    private String slug;
    private String description;
    private LocalDateTime createdAt;
    private String thumbnailImage;
    private boolean inPrivate;
    private boolean publishStatus;
    private Long viewCount;
    private Integer likeCount;
    private String tagString;
    private List<Comment> comments;

    @Builder
    public PostCardDto(Long id, User user, String title, String slug,
                       String description, LocalDateTime createdAt,
                       String thumbnailImage, boolean inPrivate,
                       boolean publishStatus, Long viewCount,
                       Integer likeCount, String tagString,
                       List<Comment> comments) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.createdAt = createdAt;
        this.thumbnailImage = thumbnailImage;
        this.inPrivate = inPrivate;
        this.publishStatus = publishStatus;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.tagString = tagString;
        this.comments = comments;
    }

    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
}
