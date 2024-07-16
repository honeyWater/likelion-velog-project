package org.example.velogproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostPublishDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailImage;
    private boolean inPrivate;
    private boolean publishStatus;

    @Builder
    public PostPublishDto(Long id, String title, String slug, String description,
                          String thumbnailImage, boolean inPrivate, boolean publishStatus) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.thumbnailImage = thumbnailImage;
        this.inPrivate = inPrivate;
        this.publishStatus = publishStatus;
    }
}
