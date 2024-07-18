package org.example.velogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Comment;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.CommentDto;
import org.example.velogproject.service.CommentService;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentApiController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    /**
     * 댓글 추가 메서드
     *
     * @param postId
     * @param commentDto
     * @return redirectUrl
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        commentService.addComment(postId, commentDto);

        Post post = postService.getPostById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        String domain = post.getUser().getDomain();
        String redirectUrl = "/@" + domain + "/" + post.getSlug();
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    @PostMapping("/comments/{parentId}/replies")
    public ResponseEntity<?> addReply(@PathVariable Long parentId, @RequestBody CommentDto replyDto) {
        Comment savedComment = commentService.addReply(parentId, replyDto);
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentDto updateDto) {
        try {
            updateDto.setId(commentId);
            Comment updatedComment = commentService.updateComment(updateDto);

            Post post = postService.getPostById(updatedComment.getPost().getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
            String domain = post.getUser().getDomain();

            String redirectUrl = "/@" + domain + "/" + post.getSlug();
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 삭제 메서드
     *
     * @param commentId
     * @return redirectUrl
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentService.deleteComment(commentId);

        Post post = comment.getPost();
        String domain = post.getUser().getDomain();

        String redirectUrl = "/@" + domain + "/" + post.getSlug();
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }
}
