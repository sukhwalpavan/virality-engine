package com.virality.engine.controller;

import com.virality.engine.Comment;
import com.virality.engine.Post;
import com.virality.engine.service.CommentService;
import com.virality.engine.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PostMapping("/{postId}/comments")
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        comment.setPostId(postId);
        return commentService.addComment(comment);
    }

    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId) {
        return postService.likePost(postId);
    }
}