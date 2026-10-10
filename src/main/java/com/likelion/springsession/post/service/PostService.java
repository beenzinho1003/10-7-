package com.likelion.springsession.post.service;
import com.likelion.springsession.post.dto.PostCreateRequest;
import com.likelion.springsession.post.dto.PostDetailResponse;
import com.likelion.springsession.post.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import com.likelion.springsession.post.dto.PostSummaryResponse;
import com.likelion.springsession.post.entity.Post;
import com.likelion.springsession.post.exception.PostAccessDeniedException;
import com.likelion.springsession.post.repository.PostRepository;
import com.likelion.springsession.user.entity.User;
import com.likelion.springsession.user.exception.UserNotFoundException;
import com.likelion.springsession.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostSummaryResponse> getPostSummaries() {
        List<Post> posts = postRepository.findAll();
        List<PostSummaryResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            PostSummaryResponse response = new PostSummaryResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getAuthor().getNickname(),
                    post.getCreatedAt()
            );
            responses.add(response);
        }

        return responses;
    }
    @Transactional
    public PostDetailResponse createPost(Long userId, PostCreateRequest request)
    {
        User author = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = new Post(
                author,
                request.getTitle(),
                request.getContent()
        );
        Post savedPost = postRepository.save(post);
        return toDetailResponse(savedPost);
    }
    private PostDetailResponse toDetailResponse(Post post)
    {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.getCreatedAt()
        );
    }
    public PostDetailResponse getPost(Long postId) {
        Post post = findPostById(postId);
        return toDetailResponse(post);
    }
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow();
    }
    @Transactional
    public PostDetailResponse updatePost(
            Long userId,
            Long postId,
            PostUpdateRequest request
    ) {
        Post post = findPostById(postId);
        if (!post.isAuthor(userId)) {
            throw new PostAccessDeniedException();
        }
        post.update(
                request.getTitle(),
                request.getContent()
        );

        return toDetailResponse(post);
    }
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = findPostById(postId);
        if (!post.isAuthor(userId)) {
            throw new PostAccessDeniedException();
        }

        postRepository.delete(post);
    }
}
