package org.crudfeed.core.service;

import org.crudfeed.core.dto.post.ViewPostRequestDto;
import org.crudfeed.core.entity.Post;
import org.crudfeed.core.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public String createPost(long userId, String content) {
        if (content == null || content.isBlank()) {
            return "Post cannot be empty!";
        }

        if (content.length() > 2000) {
            return "Post is too long! Max length is 2000 characters!";
        }

        Post post = Post.builder()
                .userId(userId)
                .content(content)
                .build();

        return postRepository.createPost(post)
                ? "Post created!"
                : "Could not create post!";
    }

    public String editPost(long postId, String content, long userId) {
        if (content == null || content.isBlank()) {
            return "Post cannot be empty!";
        }

        if (content.length() > 2000) {
            return "Post is too long! Max length is 2000 characters!";
        }

        return postRepository.updatePost(postId, content, userId)
                ? "Post successfully updated!"
                : "Could not update post!";
    }


    public String deletePost(long postId, long userId) {

        return postRepository.deletePost(postId, userId)
                ? "Post successfully deleted!"
                : "Could not delete post!";
    }

    public List<ViewPostRequestDto> viewFeed() { return postRepository.viewFeed(); }

    public List<ViewPostRequestDto> showUserPostsById(long userId) {
        if (userId <= 0) {
            return List.of();
        }

        return postRepository.showUserPostsById(userId);
    }

    public List<ViewPostRequestDto> showUserLikes(long userId) {
        if (userId <= 0) {
            return List.of();
        }

        return postRepository.showUserLikes(userId);
    }

    public ViewPostRequestDto searchPostById(long postId) {
        if (postId <= 0) {
            return new ViewPostRequestDto(
                    0,
                    0,
                    "",
                    "",
                    0,
                    0,
                    null,
                    null
            );
        }

        return postRepository.searchPostById(postId);
    }

    public List<ViewPostRequestDto> searchPostByWords(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        if (content.length() > 2000) {
            return List.of();
        }

        return postRepository.searchPostByWords(content);
    }

    public String likePost(long postId, long userId) {
        return postRepository.likePost(postId, userId);
    }


    public String dislikePost(long postId, long userId) {
        return postRepository.dislikePost(postId, userId);
    }
}