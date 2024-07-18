package org.example.velogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Comment;
import org.example.velogproject.domain.Post;
import org.example.velogproject.dto.CommentDto;
import org.example.velogproject.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentApiController {
    private final CommentService commentService;

    /**
     * 댓글 추가 메서드
     *
     * @param postId
     * @param commentDto
     * @return redirectUrl
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        Post post = commentService.addComment(postId, commentDto).getPost();
        String domain = post.getUser().getDomain();

        String redirectUrl = "/@" + domain + "/" + post.getSlug();
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    @PostMapping("/comments/{parentId}/replies")
    public ResponseEntity<?> addReply(@PathVariable Long parentId, @RequestBody CommentDto replyDto) {
        Post post = commentService.addReply(parentId, replyDto).getPost();
        String domain = post.getUser().getDomain();

        String redirectUrl = "/@" + domain + "/" + post.getSlug();
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
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
            Post post = commentService.updateComment(updateDto).getPost();
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

        Post post = comment.getPost();
        String domain = post.getUser().getDomain();
        commentService.deleteComment(commentId);

        String redirectUrl = "/@" + domain + "/" + post.getSlug();
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }
}
