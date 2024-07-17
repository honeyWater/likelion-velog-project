package org.example.velogproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.PostPublishDto;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.example.velogproject.util.PostUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController {
    private final UserService userService;
    private final PostService postService;
    private final JwtTokenizer jwtTokenizer;
    private final PostUtil postUtil;

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
                String writeOrPublish = post.getThumbnailImage();
                Post createdPost = postService.savePostFirstTemporarily(post);

                String redirectUrl = postUtil.setRedirectUrl(writeOrPublish, createdPost.getId());
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
            post.setDescription(postUtil.generateDescription(post.getContent()));
            Post updatedPost = postService.updatePostTemporarily(post); // 기존 게시글 업데이트

            // 임시저장에 사용하지 않는 String 값을 이용
            String redirectUrl = postUtil.setRedirectUrl(post.getThumbnailImage(), updatedPost.getId());
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 게시글 content 내 이미지 처리
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String fileName = postUtil.generateFileName(file.getOriginalFilename());
            postUtil.saveFile(file, fileName, "post_image/");

            // WebConfig 에서 설정한 리소스 핸들러 경로를 사용
            String imageUrl = "/post_image/" + fileName;
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("이미지 업로드에 실패했습니다.");
        }
    }

    // 썸네일 업로드 처리
    @PostMapping("/upload-thumbnail")
    public ResponseEntity<?> uploadThumbnail(@RequestParam("thumbnail") MultipartFile file,
                                             @RequestParam("postId") Long postId) {
        try {
            String fileName = postUtil.uploadThumbnail(file);
            postService.savePostThumbnail(fileName, postId);

            String redirectUrl = "/publish?id=" + postId;
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("썸네일 업로드에 실패했습니다");
        }
    }

    // 썸네일 재업로드 처리
    @PostMapping("/reupload-thumbnail")
    public ResponseEntity<?> reuploadThumbnail(@RequestParam("thumbnail") MultipartFile file,
                                               @RequestParam("postId") Long postId) {
        try {
            // 기존 썸네일 파일 삭제
            postService.deleteExistingThumbnail(postId);

            // 새 썸네일 업로드
            String fileName = postUtil.generateFileName(file.getOriginalFilename());
            postUtil.saveFile(file, fileName, "thumbnail_image/");

            // DB 업데이트
            postService.savePostThumbnail(fileName, postId);

            String redirectUrl = "/publish?id=" + postId;
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("썸네일 재업로드에 실패했습니다.");
        }
    }

    // 썸네일 삭제 (로컬에서 파일 삭제 및 DB에서 null 처리)
    @PostMapping("/remove-thumbnail")
    public ResponseEntity<?> removeThumbnail(@RequestBody Map<String, Long> body) {
        Long postId = body.get("postId");
        try {
            postService.deleteExistingThumbnail(postId);
            postService.removePostThumbnail(postId);

            String redirectUrl = "/publish?id=" + postId;
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("썸네일 삭제에 실패했습니다.");
        }
    }

    // 게시글 실제 출간
    @PostMapping("/save")
    public ResponseEntity<?> publishPost(@RequestBody PostPublishDto publishDto) {
        // 업데이트 수행
        Post savedPost = postService.publishPost(publishDto);

        // post id로 저장된 user, slug 를 얻어서 /@{domain}/{slug} 로 이동
        String domain = savedPost.getUser().getDomain();
        String slug = savedPost.getSlug();
        String redirectUrl = "/@" + domain + "/" + slug;

        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

            // 게시글 내용에서 이미지 파일 경로 추출
            List<String> imagePaths = postUtil.extractImagePaths(post.getContent());

            // 로컬에서 이미지 파일 삭제
            for (String imagePath : imagePaths) {
                postUtil.deleteImageFile("post_image/" + imagePath);
            }
            // 로컬에서 썸네일 삭제 및 DB 에서 게시글 삭제
            postService.deleteExistingThumbnail(postId);
            postService.deletePost(postId);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
