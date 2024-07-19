package org.example.velogproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private UserCommentDto user;
    private String comment;
    private LocalDateTime createdAt;
    private Long parentCommentId;
    private List<CommentDto> replies;
}
