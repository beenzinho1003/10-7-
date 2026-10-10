package com.likelion.springsession.comment.entity;


import com.likelion.springsession.post.entity.Post;
import com.likelion.springsession.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public Comment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        post.getComments().add(this);
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
    public void update(String content) {
        this.content = content;
    }
    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}
