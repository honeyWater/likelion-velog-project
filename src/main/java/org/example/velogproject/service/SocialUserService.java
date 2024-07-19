package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.SocialUser;
import org.example.velogproject.repository.SocialUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialUserService {
    private final SocialUserRepository socialUserRepository;

    @Transactional
    //소셜에서 정보를 가져와서 저장하거나,  이미 있다면 수정하거나..
    public SocialUser saveOrUpdateUser(String socialId, String provider,
                                       String username, String avatarUrl) {

        Optional<SocialUser> existingUser = socialUserRepository.findBySocialIdAndProvider(socialId, provider);
        SocialUser socialUser;

        if (existingUser.isPresent()) {
            //이미 소셜유저 정보를 가진 사용자라면...
            socialUser = existingUser.get();
            socialUser.setUsername(username);
            socialUser.setAvatarUrl(avatarUrl);
        } else {
            socialUser = new SocialUser();
            socialUser.setSocialId(socialId);
            socialUser.setUsername(username);
            socialUser.setAvatarUrl(avatarUrl);
            socialUser.setProvider(provider);
        }

        return socialUserRepository.save(socialUser);
    }
}
