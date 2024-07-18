package org.example.velogproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCommentDto {
    private Long id;
    private String username;
    private String domain;
    private String profileImage;
}
