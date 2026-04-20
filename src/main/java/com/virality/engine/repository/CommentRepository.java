package com.virality.engine.repository;

import com.virality.engine.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPostIdAndAuthorType(Long postId, String authorType);
}