package org.example.velogproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController {
    private final UserService userService;
    private final PostService postService;
    private final JwtTokenizer jwtTokenizer;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 새로 생성하는 게시글 임시 저장 - permitAll x
    @PostMapping("/first-temporary-save")
    public ResponseEntity<?> savePostTemporarilyFirst(@RequestBody Post post, HttpServletRequest request) {
        try {
            String accessToken = jwtTokenizer.getAccessToken(request)
                .orElseThrow(() -> new RuntimeException("Access token not found"));
            Long userId = jwtTokenizer.getUserIdFromToken(accessToken);
            Optional<User> user = userService.getUserById(userId);

            if (user.isPresent()) {
                user.ifPresent(post::setUser);
                Post createdPost = postService.savePostFirstTemporarily(post);
                String redirectUrl = "/write?id=" + createdPost.getId();
                return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 생성된 게시글을 임시 저장 - permitAll x
    @PutMapping("/temporary-save")
    public ResponseEntity<?> savePostTemporarily(@RequestBody Post post) {
        try {
            // description 설정 부분
            if (post.getContent().length() > 147) {
                post.setDescription(post.getContent().substring(0, 147) + "...");
            } else {
                post.setDescription(post.getContent());
            }

            // 기존 게시글 업데이트
            Post updatedPost = postService.updatePostTemporarily(post);
            String redirectUrl = "/write?id=" + updatedPost.getId();
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 content 내 이미지 처리
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + "post_image/" + fileName;

            File destination = new File(filePath);
            file.transferTo(destination);

            // WebConfig 에서 설정한 리소스 핸들러 경로를 사용
            String imageUrl = "/post_image/" + fileName;
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("이미지 업로드에 실패했습니다.");
        }
    }
}
