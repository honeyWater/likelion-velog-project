package org.example.velogproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.Tag;
import org.example.velogproject.domain.User;

import java.time.LocalDateTime;
import java.util.Set;

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
    private Integer commentCount;
    private String tagString;

    @Builder
    public PostCardDto(Long id, User user, String title, String slug,
                       String description, LocalDateTime createdAt,
                       String thumbnailImage, boolean inPrivate,
                       boolean publishStatus, Long viewCount,
                       Integer likeCount, Integer commentCount, String tagString
    ) {
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
        this.commentCount = commentCount;
        this.tagString = tagString;
    }
}
