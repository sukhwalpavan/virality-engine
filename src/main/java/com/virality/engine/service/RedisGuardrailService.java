package com.virality.engine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisGuardrailService {

    private final StringRedisTemplate redisTemplate;

    public RedisGuardrailService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long incrementVirality(Long postId, long points) {
        String key = "post:" + postId + ":virality_score";
        Long value = redisTemplate.opsForValue().increment(key, points);
        return value == null ? 0 : value;
    }

    public boolean tryBotReplySlot(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) return false;

        if (count > 100) {
            redisTemplate.opsForValue().decrement(key);
            return false;
        }

        return true;
    }

    public void rollbackBotReplySlot(Long postId) {
        String key = "post:" + postId + ":bot_count";
        redisTemplate.opsForValue().decrement(key);
    }

    public boolean cooldownExists(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void activateCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(10));
    }

    public boolean notificationCooldownExists(Long userId) {
        String key = "user:" + userId + ":notif_cooldown";
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void activateNotificationCooldown(Long userId) {
        String key = "user:" + userId + ":notif_cooldown";
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(15));
    }

    public void pushPendingNotification(Long userId, String message) {
        String key = "user:" + userId + ":pending_notifs";
        redisTemplate.opsForList().rightPush(key, message);
    }
}