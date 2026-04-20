package com.virality.engine.service;

import com.virality.engine.Post;
import com.virality.engine.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final RedisGuardrailService redisGuardrailService;

    public PostService(PostRepository postRepository,
                       RedisGuardrailService redisGuardrailService) {
        this.postRepository = postRepository;
        this.redisGuardrailService = redisGuardrailService;
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public String likePost(Long postId) {
        redisGuardrailService.incrementVirality(postId, 20);
        return "Post liked! Virality score updated.";
    }
}