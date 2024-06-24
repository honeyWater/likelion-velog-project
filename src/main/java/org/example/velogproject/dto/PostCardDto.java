package org.example.velogproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostCardDto {
    private Long id;
    private User user;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String thumbnailImage;
    private boolean inPrivate;
    private boolean publishStatus;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;

    @Builder
    public PostCardDto(Long id, User user, String title, String content,
                       LocalDateTime createdAt, String thumbnailImage,
                       boolean inPrivate, boolean publishStatus,
                       Long viewCount, Integer likeCount, Integer commentCount
    ) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.thumbnailImage = thumbnailImage;
        this.inPrivate = inPrivate;
        this.publishStatus = publishStatus;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
