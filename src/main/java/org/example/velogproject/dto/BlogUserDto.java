package org.example.velogproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.Follow;

import java.util.Set;

@Data
@NoArgsConstructor
public class BlogUserDto {
    private Long id;
    private String username;
    private String domain;
    private String velogName;
    private String info;
    private String profileImage;
    private Set<Follow> followers;
    private Set<Follow> followings;

    @Builder
    public BlogUserDto(Long id, String username, String domain,
                       String velogName, String info, String profileImage,
                       Set<Follow> followers, Set<Follow> followings){
        this.id = id;
        this.username = username;
        this.domain = domain;
        this.velogName = velogName;
        this.info = info;
        this.profileImage = profileImage;
        this.followers = followers;
        this.followings = followings;
    }
}
