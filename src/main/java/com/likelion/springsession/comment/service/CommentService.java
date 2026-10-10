package com.likelion.springsession.comment.service;
import com.likelion.springsession.comment.dto.CommentCreateRequest;
import com.likelion.springsession.comment.dto.CommentUpdateRequest;
import com.likelion.springsession.comment.dto.CommentResponse;
import com.likelion.springsession.comment.entity.Comment;
import com.likelion.springsession.comment.repository.CommentRepository;
import com.likelion.springsession.post.entity.Post;
import com.likelion.springsession.post.repository.PostRepository;
import com.likelion.springsession.user.entity.User;
import com.likelion.springsession.user.exception.UserNotFoundException;
import com.likelion.springsession.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.likelion.springsession.comment.exception.CommentAccessDeniedException;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long userId, Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow();
        User author = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Comment saved = commentRepository.save(new Comment(post, author, request.getContent()));
        return new CommentResponse(saved);
    }
    @Transactional
    public CommentResponse updateComment(
            Long userId,
            Long postId,
            Long commentId,
            CommentUpdateRequest request
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new IllegalArgumentException();
        }
        if (!comment.isAuthor(userId)) {
            throw new CommentAccessDeniedException();
        }
        comment.update(request.getContent());
        return new CommentResponse(comment);
    }
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new IllegalArgumentException();
        }
        if (!comment.isAuthor(userId)) {
            throw new CommentAccessDeniedException();
        }
        post.getComments().removeIf(c -> c.getId().equals(commentId));
    }
    public List<CommentResponse> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow();

        List<CommentResponse> responses = new ArrayList<>();
        for (Comment comment : commentRepository.findAllByPost(post)) {
            responses.add(new CommentResponse(comment));
        }
        return responses;
    }
}
