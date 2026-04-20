package com.virality.engine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationService {

    private final StringRedisTemplate redisTemplate;

    public NotificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void handleNotification(Long userId, String message) {

        String cooldownKey = "user:" + userId + ":notif_cooldown";
        String listKey = "user:" + userId + ":pending_notifs";

        Boolean exists = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(exists)) {

            redisTemplate.opsForList().rightPush(listKey, message);
        } else {

            System.out.println("Push Notification Sent to User: " + message);


            redisTemplate.opsForValue().set(cooldownKey, "1", Duration.ofMinutes(15));
        }
    }
}