package com.virality.engine.service;

import com.virality.engine.Comment;
import com.virality.engine.Post;
import com.virality.engine.repository.CommentRepository;
import com.virality.engine.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final RedisGuardrailService redisGuardrailService;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          RedisGuardrailService redisGuardrailService,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.redisGuardrailService = redisGuardrailService;
        this.notificationService = notificationService;
    }

    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());

        if (comment.getDepthLevel() > 20) {
            throw new RuntimeException("Vertical cap exceeded: depth cannot be more than 20");
        }

        boolean botSlotTaken = false;

        try {
            if ("BOT".equalsIgnoreCase(comment.getAuthorType())) {

                botSlotTaken = redisGuardrailService.tryBotReplySlot(comment.getPostId());
                if (!botSlotTaken) {
                    throw new RuntimeException("Horizontal cap exceeded: max 100 bot replies allowed");
                }

                Post post = postRepository.findById(comment.getPostId())
                        .orElseThrow(() -> new RuntimeException("Post not found"));

                Long humanId = post.getAuthorId();

                if (redisGuardrailService.cooldownExists(comment.getAuthorId(), humanId)) {
                    throw new RuntimeException("Cooldown active: bot cannot interact with same human within 10 minutes");
                }

                redisGuardrailService.activateCooldown(comment.getAuthorId(), humanId);

                redisGuardrailService.incrementVirality(comment.getPostId(), 1);

                notificationService.handleNotification(
                        humanId,
                        "Bot " + comment.getAuthorId() + " replied to your post"
                );

            } else {
                redisGuardrailService.incrementVirality(comment.getPostId(), 50);
            }

            return commentRepository.save(comment);

        } catch (RuntimeException ex) {
            if (botSlotTaken) {
                redisGuardrailService.rollbackBotReplySlot(comment.getPostId());
            }
            throw ex;
        }
    }
}