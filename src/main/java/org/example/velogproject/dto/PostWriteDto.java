package org.example.velogproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostWriteDto {
    private Long id;
    private User user;
    private String title;
    private String content;
    private String thumbnailImage;
    private List<String> tags;
    private String description;
    private LocalDateTime createdAt;
    private String slug;
    private boolean publishStatus;
    private boolean inPrivate;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
}
