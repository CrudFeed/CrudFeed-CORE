package org.crudfeed.core.service;

import org.crudfeed.core.entity.Post;
import org.crudfeed.core.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
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

    public List<Post> viewFeed() { return postRepository.viewFeed(); }

// TODO: REWORK POST SERVICE

//
//    public List<Post> showUserPostsById(long userId) {
//        if (userId <= 0) {
//            return List.of();
//        }
//
//        return postRepository.showUserPostsById(userId);
//    }
//
//    public List<Post> showUserLikes(long userId) {
//        if (userId <= 0) {
//            return List.of();
//        }
//
//        return postRepository.showUserLikes(userId);
//    }
//
//    public Post searchPostById(long postId) {
//        if (postId <= 0) {
//            return (Post) List.of();
//        }
//
//        return postRepository.searchPostById(postId);
//    }
//
//    public List<Post> searchPostByWords(String content) {
//        if (content == null || content.isBlank()) {
//            return List.of();
//        }
//
//        if (content.length() > 2000) {
//            return  List.of();
//        }
//
//        return postRepository.searchPostByWords(content);
//    }

    public String likePost(long postId, long userId) {
        return postRepository.likePost(postId, userId);
    }


    public String dislikePost(long postId, long userId) {
        return postRepository.dislikePost(postId, userId);
    }
}