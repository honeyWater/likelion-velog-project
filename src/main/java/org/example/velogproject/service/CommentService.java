package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Comment;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.CommentDto;
import org.example.velogproject.dto.UserCommentDto;
import org.example.velogproject.repository.CommentRepository;
import org.example.velogproject.repository.PostRepository;
import org.example.velogproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 추가
    @Transactional
    public Comment addComment(Long postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(commentDto.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setComment(commentDto.getComment());
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment addReply(Long parentId, CommentDto replyDto) {
        Comment parentComment = commentRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        User user = userRepository.findById(replyDto.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Comment reply = new Comment();
        reply.setPost(parentComment.getPost());
        reply.setUser(user);
        reply.setComment(replyDto.getComment());
        reply.setParentComment(parentComment);
        reply.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(reply);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(postId);
        return comments.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // commentId 로 댓글 찾기
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    // commentId 에 해당하는 댓글 수정
    @Transactional
    public Comment updateComment(CommentDto updateDto) {
        Comment comment = getCommentById(updateDto.getId())
            .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setComment(updateDto.getComment());
        return commentRepository.save(comment);
    }

    // commentId 에 해당하는 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();

        UserCommentDto userDto = new UserCommentDto();
        userDto.setId(comment.getUser().getId());
        userDto.setUsername(comment.getUser().getUsername());
        userDto.setDomain(comment.getUser().getDomain());
        userDto.setProfileImage(comment.getUser().getProfileImage());

        dto.setUser(userDto);
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        // 필드 설정
        if (!comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
        }
        return dto;
    }
}
